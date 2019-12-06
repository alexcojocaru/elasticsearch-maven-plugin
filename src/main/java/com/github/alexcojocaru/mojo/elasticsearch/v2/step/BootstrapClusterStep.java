package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClient;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClientException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchCommand;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Validate;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Bootstrap the ES cluster with the provided initialization script, if provided.
 * 
 * @author Alex Cojocaru
 */
public class BootstrapClusterStep
        implements ClusterStep
{

    @Override
    public void execute(ClusterConfiguration config)
    {
        if (config.getPathInitScripts().isEmpty())
        {
            // nothing to do; return
            return;
        }
        
        List<String> filePaths = config.getPathInitScripts();

        for (String filePath : filePaths)
        {
            validateFile(filePath);
        }
        
        // we'll run all commands against the first node in the cluster
        try (ElasticsearchClient client = new ElasticsearchClient.Builder()
                .withInstanceConfiguration(config.getInstanceConfigurationList().get(0))
                .withHostname("localhost")
                .build())
        {
            for(String filePath : filePaths) {
                Path path = Paths.get(filePath);
                if ("json".equalsIgnoreCase(FilenameUtils.getExtension(filePath)))
                {
                    parseJson(client, config.getLog(), path);
                }
                else
                {
                    parseScript(client, config.getLog(), path);
                }
            }
        }
    }
    
    /**
     * Verify that the given file path is a valid reference to an existing file on the disk.
     * @param filePath The path of the file to validate
     */
    protected void validateFile(String filePath)
    {
        if (new File(filePath).isFile() == false)
        {
            throw new ElasticsearchSetupException(
                    "The provided init script path is not a valid file path: " + filePath);
        }
    }
    
    protected void parseJson(ElasticsearchClient client, Log log, Path path)
    {
        try
        {
            String json = new String(Files.readAllBytes(path));

            List<Map<String, Object>> commands = new ObjectMapper().readValue(
                    json,
                    new TypeReference<List<Map<String, Object>>>(){});
            commands.forEach(command ->
            {
                log.debug(String.format("Parsing command: %s", command));
                
                ElasticsearchCommand esCommand = parseMapCommand(command);
                executeInitCommand(client, log, esCommand);
            });
        }
        catch (IOException e)
        {
            throw new ElasticsearchSetupException("Cannot read the init json file", e);
        }
    }
    
    protected ElasticsearchCommand parseMapCommand(Map<String, Object> command)
    {
        ElasticsearchCommand esCommand = new ElasticsearchCommand();
        
        String methodName = (String)command.get("method");
        esCommand.setRequestMethod(ElasticsearchCommand.RequestMethod.fromName(methodName));

        String path = (String)command.get("path");
        esCommand.setRelativeUrl(path);
        
        Object payload = command.get("payload");
        if (ElasticsearchCommand.RequestMethod.DELETE == esCommand.getRequestMethod())
        {
            Validate.isTrue(payload == null, "For DELETE commands the payload should be undefined");
        }
        else
        {
            try
            {
                esCommand.setJson(new ObjectMapper().writeValueAsString(payload));
            }
            catch (JsonProcessingException e)
            {
                throw new ElasticsearchSetupException(
                        "Cannot serialize the JSON payload for command '" + command + "'",
                        e);
            }
        }
        
        return esCommand;
    }
    
    protected void parseScript(ElasticsearchClient client, Log log, Path path) {
        try (Stream<String> stream = Files.lines(path))
        {
            stream.forEach(command ->
            { 
                log.debug(String.format("Parsing command: %s", command));
                
                ElasticsearchCommand esCommand = parseStringCommand(command);
                if (esCommand.isSkip() == false)
                {
                    executeInitCommand(client, log, esCommand);
                }
            });
        }
        catch (IOException e)
        {
            throw new ElasticsearchSetupException("Cannot read the init script file", e);
        }
    }
    
    protected ElasticsearchCommand parseStringCommand(String command)
    {
        ElasticsearchCommand esCommand = new ElasticsearchCommand();

        String formattedCommand = command.trim();
        
        // skip empty lines or lines starting with '#'
        if (formattedCommand.isEmpty() || formattedCommand.charAt(0) == '#')
        {
            esCommand.setSkip(true);
        }
        else
        {
            int firstSeparatorIndex = formattedCommand.indexOf(':');
            int secondSeparatorIndex = formattedCommand.indexOf(':', firstSeparatorIndex + 1);

            if (firstSeparatorIndex == -1 || secondSeparatorIndex == -1)
            {
                throw new ElasticsearchSetupException(
                        "Command '" + command + "' in the script file is not properly formatted."
                        + " The format is: REQUEST_METHOD:path:json_script."
                        + " Ex: PUT:indexName/typeName/id:{\"shoe_size\":39, \"shoe_color\":\"orange\"}");
            }

            String methodName = formattedCommand
                    .substring(0, firstSeparatorIndex)
                    .trim();
            ElasticsearchCommand.RequestMethod method = ElasticsearchCommand.RequestMethod
                    .fromName(methodName);
            esCommand.setRequestMethod(method);

            String relativeUrl = formattedCommand
                    .substring(firstSeparatorIndex + 1, secondSeparatorIndex)
                    .trim();
            esCommand.setRelativeUrl(relativeUrl);

            String json = formattedCommand.substring(secondSeparatorIndex + 1).trim();
            esCommand.setJson(json);
        }

        return esCommand;
    }
    
    protected void executeInitCommand(ElasticsearchClient client, Log log, ElasticsearchCommand command)
    {
        String url = "/" + command.getRelativeUrl();
        String content = command.getJson();

        try
        {
            switch (command.getRequestMethod())
            {
                case PUT:
                    client.put(url, content);
                    break;
                case POST:
                    client.post(url, content, String.class);
                    break;
                case DELETE:
                    client.delete(url);
                    break;
                default:
                    throw new IllegalStateException(String.format(
                            "Unsupported request method: %s", command.getRequestMethod()));
            }
        }
        catch (ElasticsearchClientException e)
        {
            throw new ElasticsearchSetupException(
                    String.format("Cannot execute command %s", command),
                    e);
        }
    }
}

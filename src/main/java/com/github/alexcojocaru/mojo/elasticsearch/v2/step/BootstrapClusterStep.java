package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClient;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClientException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchCommand;

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
        if (StringUtils.isBlank(config.getPathInitScript()))
        {
            // nothing to do; return
            return;
        }
        
        String filePath = config.getPathInitScript();
        validateFile(filePath);
        
        // we'll run all commands against the first node in the cluster
        ElasticsearchClient client = new ElasticsearchClient.Builder()
                .withInstanceConfiguration(config.getInstanceConfigurationList().get(0))
                .withHostname("localhost")
                .build();
        
        Stream<String> stream = null;
        try
        {
            stream = Files.lines(Paths.get(filePath));
            stream.forEach(command -> executeInitCommand(client, config.getLog(), command));
        }
        catch (IOException e)
        {
            throw new ElasticsearchSetupException("Cannot read the init script file", e);
        }
        finally
        {
            if (stream != null)
            {
                stream.close();
            }
        }
    }
    
    /**
     * Verify that the given file path is a valid reference to an existing file on the disk.
     * @param filePath
     */
    private void validateFile(String filePath)
    {
        if (new File(filePath).isFile() == false)
        {
            throw new ElasticsearchSetupException(
                    "The provided init script path is not a valid file path: " + filePath);
        }
    }
    
    private void executeInitCommand(ElasticsearchClient client, Log log, String command)
    {
        log.debug(String.format("Parsing command: %s", command));
        
        ElasticsearchCommand esCommand = parseStringCommand(command);
        if (esCommand.isSkip())
        {
            return;
        }

        String url = "/" + esCommand.getRelativeUrl();
        String content = esCommand.getJson();

        try
        {
            switch (esCommand.getRequestMethod())
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
                            "Unsupported request method: %s", esCommand.getRequestMethod()));
            }
        }
        catch (ElasticsearchClientException e)
        {
            throw new ElasticsearchSetupException(
                    String.format("Cannot execute command %s", command),
                    e);
        }
    }
    
    private ElasticsearchCommand parseStringCommand(String command)
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
}

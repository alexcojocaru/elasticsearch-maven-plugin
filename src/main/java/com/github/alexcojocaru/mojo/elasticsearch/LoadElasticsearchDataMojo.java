package com.github.alexcojocaru.mojo.elasticsearch;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author alexcojocaru
 * 
 * @goal load
 * @phase pre-integration-test
 *
 */
public class LoadElasticsearchDataMojo extends AbstractMojo
{
    /**
     * @parameter
     * @required
     */
    private File scriptFile;

    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException
    {
        if (!scriptFile.isFile())
        {
            throw new MojoExecutionException(
                    "Specified json script " + scriptFile + " does not exist.");
        }
        
        List<String> lines = null;
        try
        {
            lines = (List<String>)FileUtils.readLines(scriptFile, "UTF-8");
        }
        catch (IOException e)
        {
            throw new MojoExecutionException(
                    "Cannot read the specified JSON script file: " + scriptFile, e);
        }
        
        CloseableHttpClient client = null;
        try
        {
            client = HttpClients.createDefault();
            String host = String.format(
                    "http://%s:%d", "localhost", ElasticSearchNode.getHttpPort());            
            
            for (int i = 0; i < lines.size(); i++)
            {
                ElasticsearchCommand command = parseLine(i, lines.get(i));
                if (command == null)
                {
                    continue;
                }
                
                executeCommand(client, host, command);
            }
        }
        catch (Exception e)
        {
            throw new MojoExecutionException(
                    "Error executing the HTTP request against the ES server: " + e.getMessage(), e);
        }
        finally
        {
            if (client != null)
            {
                try
                {
                    client.close();
                }
                catch (IOException e)
                {
                    throw new MojoExecutionException("Cannot close the HTTP client", e);
                }
            }
        }
    }
    
    /**
     * @param client
     * @param host
     * @param command
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    private void executeCommand(CloseableHttpClient client, String host,
            ElasticsearchCommand command) throws IOException
    {
        String url = host + "/" + command.getRelativeUrl();
        
        StringEntity entity = new StringEntity(command.getJson(), ContentType.APPLICATION_JSON);
        
        HttpUriRequest request;
        switch (command.getRequestMethod())
        {
            case PUT:
                request = new HttpPut(url);
                break;
            case POST:
                request = new HttpPost(url);
                break;
            case DELETE:
                request = new HttpDelete(url);
                break;
            default:
                throw new IllegalStateException("Unsupported request method: "
                        + command.getRequestMethod());
        }
        
        if (request instanceof HttpEntityEnclosingRequest)
        {
            ((HttpEntityEnclosingRequest)request).setEntity(entity);
        }
        
        getLog().info("Executing command: " + command.toString());
        
        CloseableHttpResponse response = client.execute(request);
        try
        {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200 && statusCode != 201)
            {
                throw new IOException("Received error while executing command " + command
                        + EntityUtils.toString(response.getEntity()));
            }
        }
        finally
        {
            response.close();
        }
    }

    private ElasticsearchCommand parseLine(int lineNumber, String line)
            throws MojoExecutionException
    {
        line = line.trim();
        
        // Empty line, nothing to do here.
        if (line.isEmpty())
        {
            return null;
        }
        
        // Lines starting with '#' are ignored.
        if (line.charAt(0) == '#')
        {
            return null;
        }
        
        int firstSeparatorIndex = line.indexOf(':');
        int secondSeparatorIndex = line.indexOf(':', firstSeparatorIndex + 1);
        
        if (firstSeparatorIndex == -1 || secondSeparatorIndex == -1)
        {
            throw new MojoExecutionException(
                    "Line #" + lineNumber + " in the script file is not properly formatted."
                    + " The format is: REQUEST_METHOD:path:json_script."
                    + " Ex: PUT:indexName/typeName/id:{\"shoe_size\":42, \"name\":\"alex\"}"
                    + " Offending line: " + line);
        }
        
        ElasticsearchCommand command = new ElasticsearchCommand();

        String requestMethod = line.substring(0, firstSeparatorIndex);
        command.setRequestMethod(ElasticsearchCommand.RequestMethod.fromName(requestMethod));
        
        String relativeUrl = line.substring(firstSeparatorIndex + 1, secondSeparatorIndex).trim();
        command.setRelativeUrl(relativeUrl);
        
        String json = line.substring(secondSeparatorIndex + 1).trim();
        command.setJson(json);
        
        return command;
    }

}

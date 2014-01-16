package com.pingconnect.mojo.elasticsearch;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;

/**
 * Goal which starts a local Elasticsearch node.
 * 
 * @author alexcojocaru
 * 
 * @goal start
 * @phase pre-integration-test
 */
public class StartElasticsearchNodeMojo extends AbstractMojo
{
    /**
     * @parameter default-value="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /**
     * @parameter default-value="elasticsearch-data"
     */
    private String dataDirname;

    /**
     * @parameter default-value="elasticsearch-logs"
     */
    private String logsDirname;

    /**
     * @parameter
     * @required
     */
    private String clusterName;

    /**
     * @parameter
     * @required
     */
    private Integer tcpPort;

    /**
     * @parameter
     * @required
     */
    private Integer httpPort;

    public void execute() throws MojoExecutionException
    {
        File dataDirectory = prepareDirectory(outputDirectory, dataDirname, "data directory");
        File logsDirectory = prepareDirectory(outputDirectory, logsDirname, "logs directory");
        
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", clusterName)
                .put("action.auto_create_index", false)
                .put("transport.tcp.port", tcpPort)
                .put("http.port", httpPort)
                .put("path.data", dataDirectory.getAbsolutePath())
                .put("path.logs", logsDirectory.getAbsolutePath())
                .build();
        
        ElasticSearchNode.start(settings);
    }
    
    /**
     * 
     * @param parentDir
     * @param dirname
     * @param purpose What the directory is used for (i.e. 'data directory', 'logs directory., etc).
     * @throws MojoExecutionException
     */
    private File prepareDirectory(File parentDir, String dirname, String purpose)
            throws MojoExecutionException
    {
        File dir = new File(parentDir, dirname);

        // If the directory already exists, delete it.
        if (dir.exists())
        {
            try
            {
                FileUtils.deleteDirectory(dir);
            }
            catch (IOException e)
            {
                throw new MojoExecutionException(
                        "Cannot delete the existing Elasticsearch " + purpose + " "
                        + dir.getAbsolutePath(), e);
            }
        }
        
        // Create a new Elasticsearch directory.
        if (!dir.mkdirs())
        {
            throw new MojoExecutionException(
                    "Cannot create the Elasticsearch " + purpose + " "
                    + dir.getAbsolutePath());
        }
        
        return dir;
    }
}

package com.github.alexcojocaru.mojo.elasticsearch;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.settings.Settings;

/**
 * Abstract class to support starting a local Elasticsearch node.
 * 
 * @author alexcojocaru
 */
public class AbstractStartElasticsearchNodeMojo extends AbstractMojo
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

    /**
     * @parameter default-value=""
     */
    private String configPath;

    /**
     * @parameter default-value=""
     */
    private String pluginsPath;

    public void execute() throws MojoExecutionException
    {
        File dataDirectory = prepareDirectory(outputDirectory, dataDirname, "data directory");
        File logsDirectory = prepareDirectory(outputDirectory, logsDirname, "logs directory");

        Builder builder = ImmutableSettings.settingsBuilder()
                .put("cluster.name", clusterName)
                .put("action.auto_create_index", false)
                .put("transport.tcp.port", tcpPort)
                .put("http.port", httpPort)
                .put("path.data", dataDirectory.getAbsolutePath())
                .put("path.logs", logsDirectory.getAbsolutePath());

        if (configPath != null && configPath.trim().length() > 0 && new File(configPath).exists())
        {
            builder.put("path.conf", configPath);
        }
        if (pluginsPath != null && pluginsPath.trim().length() > 0 && new File(pluginsPath).exists())
        {
            builder.put("path.plugins", pluginsPath);
        }
        
        Settings settings = builder.build();
        
        ElasticsearchNode.start(settings);
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

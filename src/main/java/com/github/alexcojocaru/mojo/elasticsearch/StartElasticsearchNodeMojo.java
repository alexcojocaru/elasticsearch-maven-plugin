package com.github.alexcojocaru.mojo.elasticsearch;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.elasticsearch.common.settings.Settings;

/**
 * Goal which starts a local Elasticsearch node.
 *
 * @author gfernandes
 *
 * @goal start
 * @phase pre-integration-test
 */
public class StartElasticsearchNodeMojo extends AbstractElasticsearchNodeMojo {

    /**
     * @parameter default-value="${project.build.directory}"
     * @required
     */
    protected File outputDirectory;

    /**
     * @parameter default-value="false"
     */
    protected boolean keepData;

    /**
     * @parameter default-value="elasticsearch-data"
     */
    protected String dataDirname;

    /**
     * @parameter default-value="elasticsearch-logs"
     */
    protected String logsDirname;

    /**
     * @parameter default-value=""
     */
    protected String configPath;

    /**
     * @parameter default-value=""
     */
    protected String pluginsPath;

    /**
     * @parameter default-value=false
     */
    protected boolean autoCreateIndex;

    /**
     * @parameter
     * @required
     */
    protected Integer tcpPort;

    @Override
    public void execute() throws MojoExecutionException
    {
        if (!skip) {
            File dataDirectory = prepareDirectory(outputDirectory, dataDirname, "data directory");
            File logsDirectory = prepareDirectory(outputDirectory, logsDirname, "logs directory");

            Settings.Builder builder = Settings.settingsBuilder()
                    .put("cluster.name", clusterName)
                    .put("action.auto_create_index", autoCreateIndex)
                    .put("transport.tcp.port", tcpPort)
                    .put("http.port", httpPort)
                    // ES v2.0.0 requires this property; set it to the parent of the data/log dirs.
                    .put("path.home", outputDirectory.getAbsolutePath())
                    .put("path.data", dataDirectory.getAbsolutePath())
                    .put("path.logs", logsDirectory.getAbsolutePath());

            if (configPath != null && configPath.trim().length() > 0 && new File(configPath).exists()) {
                builder.put("path.conf", configPath);
            }
            if (pluginsPath != null && pluginsPath.trim().length() > 0 && new File(pluginsPath).exists()) {
                builder.put("path.plugins", pluginsPath);
            }


            Settings settings = builder.build();
            startNode(settings);
        }
    }

    protected ElasticsearchNode startNode(Settings settings) throws MojoExecutionException
    {
        if (getNode() != null)
        {
            return getNode();
        }
        else
        {
            ElasticsearchNode elasticsearchNode = new ElasticsearchNode(settings);
            super.getPluginContext().put(clusterName, elasticsearchNode);
            return elasticsearchNode;
        }
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
            if (keepData)
            {
                return dir;
            }

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

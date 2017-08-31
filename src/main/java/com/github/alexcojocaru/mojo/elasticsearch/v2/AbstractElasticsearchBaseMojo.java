package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.File;

import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;

import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.ChainedArtifactResolver;
import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.ElasticsearchBaseConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.PluginArtifactResolver;

/**
 * Base mojo to define maven parameters required by all ES mojos.
 * 
 * @author Alex Cojocaru
 */
public abstract class AbstractElasticsearchBaseMojo
        extends AbstractMojo
        implements ElasticsearchBaseConfiguration
{
    /**
     * The number of Elasticsearch nodes to start within the cluster.
     */
    @Parameter(property="es.instanceCount", defaultValue = "1")
    protected int instanceCount;

    /**
     * The Elasticsearch base path.
     */
    @Parameter(defaultValue = "${project.build.directory}/elasticsearch", readonly = true)
    protected File baseDir;

    /**
     * Whether to skip the plugin execution or not.
     */
    @Parameter(property="es.skip", defaultValue = "false")
    protected boolean skip;

    /**
     * The plugin log level.
     */
    @Parameter(property="es.logLevel", defaultValue = "INFO")
    protected String logLevel;
    
    private Log log;
    
    
    @Override
    public int getInstanceCount()
    {
        return instanceCount;
    }

    public void setInstanceCount(int instanceCount)
    {
        this.instanceCount = instanceCount;
    }

    @Override
    public File getBaseDir()
    {
        return baseDir;
    }

    public void setBaseDir(File baseDir)
    {
        this.baseDir = baseDir;
    }

    @Override
    public boolean isSkip()
    {
        return skip;
    }

    public void setSkip(boolean skip)
    {
        this.skip = skip;
    }

    @Override
    public String getLogLevel()
    {
        return logLevel;
    }

    public void setLogLevel(String logLevel)
    {
        this.logLevel = logLevel;
    }
    
    private int getMavenLogLevel()
    {
        switch (logLevel)
        {
            case "DEBUG":
                return Logger.LEVEL_DEBUG;
            case "WARN":
                return Logger.LEVEL_WARN;
            case "ERROR":
                return Logger.LEVEL_ERROR;
            case "FATAL":
                return Logger.LEVEL_FATAL;
            case "DISABLED":
                return Logger.LEVEL_DISABLED;
            case "INFO":
            default:
                return Logger.LEVEL_INFO;
        }
    }

    @Override
    public Log getLog()
    {
        if (log == null)
        {
            log = new DefaultLog(new ConsoleLogger(getMavenLogLevel(), "console"));
        }
        return log;
    }

    @Override
    public ClusterConfiguration buildClusterConfiguration()
    {
        ClusterConfiguration.Builder clusterConfigBuilder = new ClusterConfiguration.Builder()
                .withArtifactResolver(buildArtifactResolver())
                .withLog(getLog());

        for (int i = 0; i < instanceCount; i++)
        {
            clusterConfigBuilder.addInstanceConfiguration(new InstanceConfiguration.Builder()
                    .withId(i)
                    .withBaseDir(baseDir.getAbsolutePath() + i)
                    .build());
        }
        
        ClusterConfiguration clusterConfig = clusterConfigBuilder.build();
        
        return clusterConfig;
    }

    @Override
    public PluginArtifactResolver buildArtifactResolver()
    {
        ChainedArtifactResolver artifactResolver = new ChainedArtifactResolver();
        return artifactResolver;
    }

}

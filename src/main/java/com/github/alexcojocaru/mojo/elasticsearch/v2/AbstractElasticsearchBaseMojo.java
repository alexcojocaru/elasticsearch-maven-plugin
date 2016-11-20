package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

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
    @Parameter(defaultValue = "1")
    protected int instanceCount;

    /**
     * The Elasticsearch base path.
     */
    @Parameter(defaultValue = "${project.build.directory}/elasticsearch", readonly = true)
    protected File baseDir;

    /**
     * Whether to skip the plugin execution or not.
     */
    @Parameter(defaultValue = "false")
    protected boolean skip;
    
    
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

package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
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
     * The number of Elasticsearch nodes to start within the cluster. Only a single node is
     * supported at the moment.
     */
    @Parameter(defaultValue = "1", readonly = true)
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
    public List<InstanceConfiguration> buildInstanceConfigurationList()
    {
        InstanceConfigurationUtil.validateInstanceCount(instanceCount);

        List<InstanceConfiguration> configList = new ArrayList<>();
        for (int i = 0; i < instanceCount; i++)
        {
            InstanceConfiguration config = new InstanceConfiguration.Builder()
                    .withId(i)
                    .withBaseDir(baseDir.getAbsolutePath() + i)
                    .build();
            configList.add(config);
        }

        return configList;
    }

    @Override
    public PluginArtifactResolver buildArtifactResolver()
    {
        ChainedArtifactResolver artifactResolver = new ChainedArtifactResolver();
        return artifactResolver;
    }

    @Override
    public Log getLog()
    {
        return super.getLog();
    }

}

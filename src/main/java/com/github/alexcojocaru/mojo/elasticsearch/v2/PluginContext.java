package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.util.List;

import org.apache.maven.plugin.logging.Log;

import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.PluginArtifactResolver;

/**
 * The plugin context, containing the list of ES configurations, the artifact resolver, the logger.
 * 
 * @author Alex Cojocaru
 */
public class PluginContext
{
    protected List<InstanceConfiguration> configurationList;
    protected PluginArtifactResolver artifactResolver;
    protected Log log;

    public PluginContext(List<InstanceConfiguration> configurationList,
            PluginArtifactResolver artifactResolver,
            Log log)
    {
        this.configurationList = configurationList;
        this.artifactResolver = artifactResolver;
        this.log = log;
    }

    public List<InstanceConfiguration> getConfigurationList()
    {
        return configurationList;
    }

    public PluginArtifactResolver getArtifactResolver()
    {
        return artifactResolver;
    }

    public Log getLog()
    {
        return log;
    }

}

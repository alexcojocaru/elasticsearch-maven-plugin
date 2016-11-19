package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.apache.maven.plugin.logging.Log;

import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.PluginArtifactResolver;

/**
 * The context (including the configuration, the artifact resolver, the logger) to use for
 * running/stopping ES instances.
 * 
 * @author Alex Cojocaru
 */
public class InstanceContext
{
    protected InstanceConfiguration configuration;
    protected PluginArtifactResolver artifactResolver;
    protected Log log;

    public InstanceContext(InstanceConfiguration configuration,
            PluginArtifactResolver artifactResolver,
            Log log)
    {
        this.configuration = configuration;
        this.artifactResolver = artifactResolver;
        this.log = log;
    }

    public InstanceConfiguration getConfiguration()
    {
        return configuration;
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

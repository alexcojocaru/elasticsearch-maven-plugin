package com.github.alexcojocaru.mojo.elasticsearch.v2.configuration;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.PluginContext;

/**
 * The base configuration of an ES mojo.
 * 
 * @author Alex Cojocaru
 *
 */
public interface ElasticsearchBaseConfiguration
{
    int getInstanceCount();
    
    File getBaseDir();
    
    boolean isSkip();
    
    List<InstanceConfiguration> buildInstanceConfigurationList();
    
    PluginArtifactResolver buildArtifactResolver();
    
    Log getLog();
    
    
    public default PluginContext buildContext()
    {
        PluginContext context = new PluginContext(buildInstanceConfigurationList(),
                buildArtifactResolver(),
                getLog());
        
        return context;
    }
}

package com.github.alexcojocaru.mojo.elasticsearch.v2.configuration;

import java.io.File;

import org.apache.maven.plugin.logging.Log;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;

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
    
    String getLogLevel();
    

    ClusterConfiguration buildClusterConfiguration();
    
    PluginArtifactResolver buildArtifactResolver();
    
    Log getLog();
    
}

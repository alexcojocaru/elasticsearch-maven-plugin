package com.github.alexcojocaru.mojo.elasticsearch.v2.configuration;

import java.util.List;

import com.github.alexcojocaru.mojo.elasticsearch.v2.PluginConfiguration;

/**
 * The more complete configuration of an ES mojo.
 * 
 * @author Alex Cojocaru
 *
 */
public interface ElasticsearchConfiguration extends ElasticsearchBaseConfiguration
{
    String getVersion();
    
    String getClusterName();
    
    int getHttpPort();
    
    int getTransportPort();
    
    String getPathConf();
    
    String getPathData();
    
    String getPathLogs();
    
    List<PluginConfiguration> getPlugins();
    
    List<String> getPathInitScript();
    
    boolean isKeepExistingData();
    
    int getInstanceStartupTimeout();

    int getClusterStartupTimeout();
    
    boolean isSetAwait();
    
    boolean isAutoCreateIndex();
}

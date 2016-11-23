package com.github.alexcojocaru.mojo.elasticsearch.v2.configuration;

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
    
    String getPathData();
    
    String getPathLogs();
    
    String getPathInitScript();
    
    boolean isKeepExistingData();
    
    int getTimeout();
    
    boolean isSetAwait();
    
    boolean isAutoCreateIndex();
}

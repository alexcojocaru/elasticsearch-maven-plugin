package com.github.alexcojocaru.mojo.elasticsearch;

import org.apache.maven.plugin.MojoExecutionException;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

/**
 * @author alexcojocaru
 *
 */
public class ElasticsearchNode
{
    private static Node node;
    private static int httpPort;
    
    /**
     * Start a local ES node with the given settings.
     * <br>
     * If the local node is already running prior to calling this method,
     * an IllegalStateException will be thrown.
     * @param settings
     * @throws MojoExecutionException 
     */
    public static void start(Settings settings) throws MojoExecutionException
    {
        if (node != null)
        {
            throw new MojoExecutionException("A local node is already running");
        }

        // Set the node to be as lightweight as possible,
        // at the same time being able to be discovered from an external JVM.
        settings = ImmutableSettings.settingsBuilder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0)
                .put("network.host", "127.0.0.1")
                .put("discovery.zen.ping.timeout", "3ms")
                .put("discovery.zen.ping.multicast.enabled", false)
                .put("http.cors.enabled", true)
                .put(settings)
                .build();
        
        httpPort = settings.getAsInt("http.port", 9200);
        
        node = NodeBuilder.nodeBuilder().settings(settings).node();
    }
    
    /**
     * Start a local ES node with default settings.
     * <br>
     * If the local node is already running prior to calling this method,
     * an IllegalStateException will be thrown.
     * @param dataPath
     * @throws MojoExecutionException 
     */
    public static void start(String dataPath) throws MojoExecutionException
    {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "test")
                .put("action.auto_create_index", false)
                .put("transport.tcp.port", 9300)
                .put("http.port", 9200)
                .put("path.data", dataPath)
                .build();
        
        start(settings);
    }
    
    /**
     * Always return a new client connected to the local ES node.
     * @return
     */
    public static Client getClient()
    {
        return node.client();
    }
    
    /**
     * Close the ES node.
     * <br>
     * If the node is already stopped or closed, this method is a no-op.
     */
    public static void stop()
    {
        if (node == null)
        {
            return;
        }
        
        node.stop();
        node.close();
        node = null;
    }

    /**
     * @return the httpPort
     */
    public static int getHttpPort()
    {
        return httpPort;
    }

}

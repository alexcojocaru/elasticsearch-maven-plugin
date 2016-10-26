package com.github.alexcojocaru.mojo.elasticsearch;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.maven.plugin.MojoExecutionException;
import org.elasticsearch.cli.Terminal;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.logging.LogConfigurator;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.node.internal.InternalSettingsPreparer;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.Netty4Plugin;

/**
 * @author alexcojocaru
 *
 */
public class ElasticsearchNode
{

    public static class PluginNode extends Node {
        public PluginNode(Environment environment) {
            super(environment, Collections.<Class<? extends Plugin>>singletonList(Netty4Plugin.class));
        }
    }

    private Node node;
    private int httpPort;

    private ElasticsearchNode(Environment environment) throws MojoExecutionException
    {
        httpPort = environment.settings().getAsInt("http.port", 9200);

        // Must use this constructor to get proper environment initialization
        node = new PluginNode(environment);

        try {
            node.start();
        } catch (NodeValidationException e) {
             throw new MojoExecutionException( "Error starting the ES node", e );
        }

        // Set the indices to be as lightweight as possible by default
        Settings indexTemplateSettings = Settings.builder()
                .put("number_of_shards", 1)
                .put("number_of_replicas", 0)
                .build();
        getClient().admin().indices()
                .preparePutTemplate( "lightweight_index" )
                .setTemplate( "*" )
                .setSettings( indexTemplateSettings )
                .execute()
                .actionGet();
    }

    /**
     * Start a local ES node with default settings.
     * <br>
     * If the local node is already running prior to calling this method,
     * an IllegalStateException will be thrown.
     * @param dataPath
     * @throws MojoExecutionException
     * @return an instance of an ElasticsearchNode
     */
    public static ElasticsearchNode start(String dataPath) throws MojoExecutionException
    {
        return start(dataPath, 9200, 9300);
    }

    /**
     * Start a local ES node with default settings.
     * <br>
     * If the local node is already running prior to calling this method,
     * an IllegalStateException will be thrown.
     * @param dataPath
     * @param httpPort
     * @param tcpPort
     * @throws MojoExecutionException
     * @return an instance of an ElasticsearchNode
     */
    public static ElasticsearchNode start(String dataPath, int httpPort, int tcpPort)
            throws MojoExecutionException
    {
        // ES v2.0.0 requires the path.home property.
        // Set it to the parent of the data directory.
        String homePath = new File(dataPath).getParent();

        Settings settings = Settings.builder()
                .put("cluster.name", "test")
                .put("action.auto_create_index", false)
                .put("transport.tcp.port", tcpPort)
                .put("http.port", httpPort)
                .put("path.data", dataPath)
                .put("path.home", homePath)
                .build();

        return start(settings);
    }

    /**
     * Start a local ES node with the given settings.
     * <br>
     * If the local node is already running prior to calling this method,
     * an IllegalStateException will be thrown.
     * @param settings
     * @throws MojoExecutionException
     */
    public static ElasticsearchNode start(Settings settings)
            throws MojoExecutionException
    {
    	settings = Settings.builder()
                .put("network.host", "127.0.0.1")
                .put("http.cors.enabled", true)
                .put("discovery.zen.fd.ping_timeout", "3ms")
                .put(settings)
                .build();
        // Must use this internal API to get ES to load the configuration files...
    	Environment environment = InternalSettingsPreparer.prepareEnvironment( settings, Terminal.DEFAULT );
        setupLogging(environment);
        return new ElasticsearchNode(environment);
    }

    /**
     * Copied from org.elasticsearch.bootstrap.Bootstrap.
     * @param settings
     */
    private static void setupLogging(Environment environment)
    {
        LogConfigurator.configureWithoutConfig( environment.settings() );
        try
        {
            Class.forName("org.apache.logging.log4j.Logger");
            LogConfigurator.configure( environment );
        } catch (ClassNotFoundException e)
        {
            // no log4j
        } catch (NoClassDefFoundError e)
        {
            // no log4j
        } catch (Exception e)
        {
            System.err.println("Failed to configure logging...");
            e.printStackTrace();
        }
    }

    /**
     * Always return a new client connected to the local ES node.
     * @return
     */
    public Client getClient()
    {
        return node.client();
    }

    /**
     * Close the ES node.
     * <br>
     * If the node is already stopped or closed, this method is a no-op.
     *
     * @throws IOException
     */
    public void stop() throws IOException
    {
        if (node != null)
        {
            node.close();
            node = null;
        }
    }

    public boolean isClosed()
    {
        return (node == null || node != null && node.isClosed());
    }

    /**
     * @return the httpPort
     */
    public int getHttpPort()
    {
        return httpPort;
    }

}

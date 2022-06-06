package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClient;

/**
 * @author Alex Cojocaru
 *
 */
public abstract class ItBase
{
    protected static int httpPort;
    protected static String clusterName;
    protected static int instanceCount;
    protected static Log log;
    
    protected ElasticsearchClient client;
    

    @BeforeClass
    public static void beforeClass()
    {
        try
        {
            Properties props = new Properties();
            props.load(new FileInputStream("test.properties"));
            httpPort = Integer.parseInt(props.getProperty("es.httpPort"));
            clusterName = props.getProperty("es.clusterName");
            instanceCount = Integer.parseInt(props.getProperty("es.instanceCount"));

            // only support for DEBUG and INFO log levels during integration testing
            int logLevel = "INFO".equalsIgnoreCase(props.getProperty("es.logLevel", "INFO"))
                    ? Logger.LEVEL_INFO
                    : Logger.LEVEL_DEBUG;
            log = new DefaultLog(new ConsoleLogger(logLevel, "console"));
        }
        catch (IOException e)
        {
            throw new RuntimeException("Cannot load properties from test.properties", e);

        }
    }
    
    @Before
    public void before()
    {
        client = new ElasticsearchClient.Builder()
                .withLog(log)
                .withHostname("localhost")
                .withPort(httpPort)
                .withSocketTimeout(5000)
                .build();
    }
    @After
    public void after()
    {
        client.close();
    }

}

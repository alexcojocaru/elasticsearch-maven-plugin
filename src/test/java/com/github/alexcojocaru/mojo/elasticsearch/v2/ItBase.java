package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.plugin.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.Mock;

import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClient;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchCredentials;

/**
 * @author Alex Cojocaru
 *
 */
public abstract class ItBase
{
    protected static int httpPort;
    protected static String clusterName;
    protected static int instanceCount;
    protected static String bootstrapPassword;
    
    protected static ElasticsearchCredentials elasticsearchCredentials;

    protected ElasticsearchClient client;
    
    @Mock
    private Log log;
    

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
            bootstrapPassword = props.getProperty("es.bootstrapPassword");
        }
        catch (IOException e)
        {
            throw new RuntimeException("Cannot load properties from test.properties", e);
        }
        
        elasticsearchCredentials = bootstrapPassword == null
                ? null
                : new ElasticsearchCredentials.Builder()
                        .withPassword(bootstrapPassword)
                        .build();
    }
    
    @Before
    public void before()
    {
        client = new ElasticsearchClient.Builder()
                .withLog(log)
                .withHostname("localhost")
                .withPort(httpPort)
                .withSocketTimeout(5000)
                .withCredentials(elasticsearchCredentials)
                .build();
    }
    @After
    public void after()
    {
        client.close();
    }

}

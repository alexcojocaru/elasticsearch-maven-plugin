package com.github.alexcojocaru.mojo.elasticsearch.v2.runforkedwithinitscript;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.apache.maven.plugin.logging.Log;

import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClient;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClientException;

/**
 * 
 * @author Alex Cojocaru
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class InitScriptTest
{
    private static int httpPort;
    
    private ElasticsearchClient client;
    
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
        }
        catch (IOException e)
        {
            throw new RuntimeException("Cannot load httpPort from test.properties", e);
        }
    }
    
    @Before
    public void before()
    {
        client = new ElasticsearchClient(log, "localhost", httpPort);
    }
    
    @Test
    public void testIndexExist() throws ElasticsearchClientException
    {
        Map index = client.get("/load_test_index", HashMap.class);
        Assert.assertTrue(index.containsKey("load_test_index"));
    }
    
    @Test
    public void testUser1Exists() throws ElasticsearchClientException
    {
        Map user = client.get("/load_test_index/test_type/1", HashMap.class);

        // the user must exist
        Assert.assertEquals(Boolean.TRUE, user.get("found"));

        // the user was updated after it was created
        Assert.assertEquals(new Integer(2), user.get("_version"));
        
        // verify the user attributes
        Map userData = (Map)user.get("_source");
        Assert.assertEquals("alexc", userData.get("name"));
        Assert.assertEquals(new Long(1388000499000L), userData.get("lastModified"));
    }
    
    @Test(expected = ElasticsearchClientException.class)
    public void testUser2DoesNotExists() throws ElasticsearchClientException
    {
        client.get("/load_test_index/test_type/2", HashMap.class);
    }
    
}
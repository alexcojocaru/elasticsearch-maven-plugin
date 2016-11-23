package com.github.alexcojocaru.mojo.elasticsearch.v2;

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
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.Monitor;

/**
 * 
 * @author Alex Cojocaru
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PathScriptsTest extends ItBase
{    
    @Test
    public void testClusterRunning()
    {
        boolean isRunning = Monitor.isClusterRunning(clusterName, client);
        Assert.assertTrue("The ES cluster should be running", isRunning);
    }
    
    @Test
    public void testScript() throws ElasticsearchClientException
    {
        // create index
        client.put(
                "/city",
                "{ \"settings\" : { \"number_of_shards\" : 1, \"number_of_replicas\" : 0 } }");

        // create mapping
        client.put(
                "/city/street/_mapping",
                "{ \"street\" : { \"properties\" : { \"name\" : { \"type\" : \"string\" } } } }");

        // index a document
        client.put("/city/street/1", "{ \"name\" : \"foo\" }");
        
        
        // la piece de resistence: run the script
        client.post(
                "/city/street/1/_update",
                "{ \"script\": { \"lang\": \"painless\", \"file\": \"name-appender\" } }",
                String.class);

        
        // get the document
        Map street = client.get("/city/street/1", HashMap.class);

        // the street must exist
        Assert.assertEquals(Boolean.TRUE, street.get("found"));
        
        // verify that the street name was modified by the script
        Map streetData = (Map)street.get("_source");
        Assert.assertEquals("foo-bar", streetData.get("name"));
    }
    
}
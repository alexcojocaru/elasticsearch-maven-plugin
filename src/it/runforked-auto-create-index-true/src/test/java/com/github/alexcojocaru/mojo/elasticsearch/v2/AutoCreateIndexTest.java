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
public class AutoCreateIndexTest extends ItBase
{    
    @Test
    public void testClusterRunning()
    {
        boolean isRunning = Monitor.isClusterRunning(clusterName, client);
        Assert.assertTrue("The ES cluster should be running", isRunning);
    }
    
    @Test
    public void testAutoCreateIndex() throws ElasticsearchClientException
    {
        // create a document in a new (non-existing) index
        client.put("/auto_test_index/test_type/1", "{ \"name\" : \"alexc\" }");
        
        Map index = client.get("/auto_test_index", HashMap.class);
        Assert.assertTrue(index.containsKey("auto_test_index"));
    
        Map user = client.get("/auto_test_index/test_type/1", HashMap.class);

        // the user must exist
        Assert.assertEquals(Boolean.TRUE, user.get("found"));
        
        // verify the user attributes
        Map userData = (Map)user.get("_source");
        Assert.assertEquals("alexc", userData.get("name"));
    }
    
}
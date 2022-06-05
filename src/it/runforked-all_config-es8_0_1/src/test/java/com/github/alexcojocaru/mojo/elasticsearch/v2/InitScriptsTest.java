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
public class InitScriptsTest extends ItBase
{    
    @Test
    public void testClusterRunning()
    {
        boolean isRunning = Monitor.isClusterRunning(log, clusterName, instanceCount, client);
        Assert.assertTrue("The ES cluster should be running", isRunning);
    }
    
    @Test
    public void testIndexesExist() throws ElasticsearchClientException
    {
        Map index = client.get("/load_test_index", HashMap.class);
        Assert.assertTrue(index.containsKey("load_test_index"));

        index = client.get("/load_test_other_index", HashMap.class);
        Assert.assertTrue(index.containsKey("load_test_other_index"));
    }
    
    @Test
    public void testUser1ExistsInIndex1() throws ElasticsearchClientException
    {
        Map user = client.get("/load_test_index/_doc/1", HashMap.class);

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
    public void testUser2DoesNotExistInIndex1() throws ElasticsearchClientException
    {
        client.get("/load_test_index/_doc/2", HashMap.class);
    }

    @Test
    public void testUser2ExistsInIndex2() throws ElasticsearchClientException
    {
        Map user = client.get("/load_test_other_index/_doc/1", HashMap.class);

        // the user must exist
        Assert.assertEquals(Boolean.TRUE, user.get("found"));

        // verify the user attributes
        Map userData = (Map)user.get("_source");
        Assert.assertEquals("otherAlex", userData.get("name"));
    }
    
}

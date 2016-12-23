package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClientException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.Monitor;

/**
 * 
 * @author Alex Cojocaru
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultsTest extends ItBase
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
        // create a document in a new (non-existing) index; it should fail
        try
        {
            client.put("/auto_test_index/test_type/1", "{ \"name\" : \"alexc\" }");
            throw new IllegalStateException(
                    "The create index request should have failed with auto_create_index disabled");
        }
        catch (ElasticsearchClientException e)
        {
            Assert.assertEquals(404, e.getStatusCode());
        }
    }
    
}
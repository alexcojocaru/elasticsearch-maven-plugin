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
        boolean isRunning = Monitor.isClusterRunning(log, clusterName, instanceCount, client);
        Assert.assertTrue("The ES cluster should be running", isRunning);
    }

    @Test
    public void testVersion() throws ElasticsearchClientException
    {
        // Fetch root resource which includes the version
        Map root = client.get("/", HashMap.class);

        Map version = (Map) root.get("version");
        Assert.assertTrue(version.containsKey("number"));

        // verify the major version
        String versionNumber = (String)version.get("number");
        Assert.assertTrue(versionNumber.equals("7.10.2"));
    }
}

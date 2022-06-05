package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

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
public class EnvConfTest extends ItBase
{
    @Test
    public void testClusterRunning()
    {
        boolean isRunning = Monitor.isClusterRunning(log, clusterName, instanceCount, client);
        Assert.assertTrue("The ES cluster should be running", isRunning);
    }

    @Test
    public void testNodeName() throws ElasticsearchClientException
    {
        // Fetch settings of all nodes
        Map results = client.get("/_nodes/settings", HashMap.class);

        // "nodes" attribute
        Map nodes = (Map)results.get("nodes");

        // there should be a single attribute in "nodes"
        Set nodesSet = (Set)nodes.entrySet();
        Assert.assertEquals(1, nodesSet.size());

        // first node's attributes
        Map nodeAttributes = (Map)((Map.Entry)nodesSet.iterator().next()).getValue();

        Assert.assertEquals("name-defined-in-pom", nodeAttributes.get("name"));

    }

}

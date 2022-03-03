package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClientException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.Monitor;

/**
 *
 * @author Brandon Smith
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class InstanceSettingsTest extends ItBase
{
    @Test
    public void testInstanceSettings() throws ElasticsearchClientException
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

        List roles = (List)nodeAttributes.get("roles");
        Assert.assertEquals(2, roles.size());
        Assert.assertTrue(roles.contains("master"));
        Assert.assertTrue(roles.contains("data"));
    }
}

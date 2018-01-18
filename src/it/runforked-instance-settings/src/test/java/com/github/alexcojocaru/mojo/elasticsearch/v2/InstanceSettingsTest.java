package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
        final Map nodes = (Map)client.get("/_nodes/settings", HashMap.class).get("nodes");
        Assert.assertEquals(3, nodes.size());
        final Set<String> nodeNames = new HashSet<String>();
        for (final Object nodeObj : nodes.values()) {
            final Map node = (Map)nodeObj;
            final String name = (String)node.get("name");
            nodeNames.add(name);
            final Map attributes = (Map)node.get("attributes");
            if ("first".equals(name)) {
                Assert.assertEquals("hot", attributes.get("data_type"));
            } else if ("second".equals(name)) {
                Assert.assertEquals("warm", attributes.get("data_type"));
            } else {
                Assert.assertNull(attributes);
            }
        }
        Assert.assertTrue(nodeNames.contains("first"));
        Assert.assertTrue(nodeNames.contains("second"));
    }
}

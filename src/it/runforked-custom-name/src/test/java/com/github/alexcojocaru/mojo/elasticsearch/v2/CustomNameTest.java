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
 * @author Brandon Smith
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomNameTest extends ItBase
{
    @Test
    public void testCustomNodeName()
    {
        final Map nodes = client.get("/_nodes/settings", HashMap.class).get("nodes");
        Assert.assertEquals(1, nodes.size())
        final Map node = nodes.values().iterator().next();
        Assert.assertEquals("custom_name", node.get("name"));
    }
}

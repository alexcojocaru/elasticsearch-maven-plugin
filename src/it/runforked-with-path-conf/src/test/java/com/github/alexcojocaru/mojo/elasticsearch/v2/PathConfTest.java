package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class PathConfTest extends ItBase
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
        // Fetch the Elasticsearch root resource which includes the node name
        Map results = client.get("/", HashMap.class);

        Assert.assertEquals("runforked-with-path-conf", results.get("name"));
    }

}
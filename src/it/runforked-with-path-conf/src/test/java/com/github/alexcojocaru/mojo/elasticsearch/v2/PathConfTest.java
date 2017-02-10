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
                "{ \"street\" : { \"properties\" : { \"name\" : { \"type\" : \"keyword\" } } } }");

        // index a document
        client.put("/city/street/1?refresh=true", "{ \"name\" : \"foo\" }");


        // la piece de resistance: run an inline groovy script
        // (which is forbidden by default, but allowed by our config file)
        Map results = client.get(
                "/city/street/_search",
                "{ \"script_fields\": { \"suffixedName\":"
                + "{ \"script\": { \"lang\": \"groovy\", \"inline\": \"doc['name'][0] + 'bar'\" } }"
                + "} }",
                HashMap.class);

        // get the first hit
        Map hits = (Map) results.get("hits");
        List hitsArray = (List) hits.get("hits");
        Map firstHit = (Map) hitsArray.get(0);
        Map firstHitFields = (Map) firstHit.get("fields");

        // the suffixed name must exist
        List expected = new ArrayList();
        expected.add( "foobar" );
        Assert.assertEquals(expected, firstHitFields.get("suffixedName"));
    }

}
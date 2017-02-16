package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.FileInputStream;
import java.io.IOException;
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
public class PluginsTest extends ItBase
{    
    @Test
    public void testClusterRunning()
    {
        boolean isRunning = Monitor.isClusterRunning(clusterName, client);
        Assert.assertTrue("The ES cluster should be running", isRunning);
    }
    
    @Test
    public void testMapperSizePlugin() throws ElasticsearchClientException
    {
        // create an index and enable the mapper _size attribute
        client.put("/plugins", "{ \"mappings\": { \"es\": { \"_size\": { \"enabled\": true } } } }");
        
        // create a couple of documents
        client.put("/plugins/es/1?refresh=true", "{ \"name\" : \"mapper-size\" }"); // _size will be 26
        client.put("/plugins/es/2?refresh=true", "{ \"name\" : \"mapper-murmur3\" }"); // _size will be 29
        
        // this should return just the first document
        Map results = client.get(
                "/plugins/_search",
                "{ \"query\": { \"term\": { \"_size\": 26 } } }",
                HashMap.class);


        Assert.assertTrue(results.containsKey("hits"));
        
        Map hits = (Map)results.get("hits");
        Assert.assertEquals(new Integer(1), hits.get("total"));
        
        List documents = (List)hits.get("hits");
        Map document = (Map)documents.get(0);
        Map source = (Map)document.get("_source");
        Assert.assertEquals("mapper-size", source.get("name"));
    }
    
    @Test
    public void testIngestUserAgentPlugin() throws ElasticsearchClientException
    {
        // create an index
        client.put("/tracking", "{ \"settings\": { } }");
        
        // enable the user-agent plugin
        client.put("/_ingest/pipeline/user_agent",
                "{ \"description\" : \"user agent info\", \"processors\" : [ { \"user_agent\" : { \"field\" : \"agent\" } } ] }");
        
        // index a document
        client.put("/tracking/user/jdoe?pipeline=user_agent&refresh=true",
                "{ \"agent\" : \"Mozilla/5.0 (X11; Linux x86_64; rv:50.0) Gecko/20100101 Firefox/50.0\" }");
        
        
        // get the user and observe the auto generated fields/values
        Map result = client.get("/tracking/user/jdoe", HashMap.class);
        Assert.assertEquals(true, result.get("found"));
        
        Map source = (Map)result.get("_source");
        Map userAgent = (Map)source.get("user_agent");
        Assert.assertEquals("Firefox", userAgent.get("name"));
        Assert.assertEquals("Linux", userAgent.get("os_name"));
    }
    
    @Test
    public void testIngestAttachmentPlugin() throws ElasticsearchClientException
    {
        // create an index
        client.put("/attachment", "{ \"settings\": { } }");
        
        // enable the user-agent plugin
        client.put("/_ingest/pipeline/attachment",
                "{ \"description\" : \"attachment info\", \"processors\" : [ { \"attachment\" : { \"field\" : \"data\" } } ] }");
        
        // index a document
        client.put("/attachment/email/1?pipeline=attachment&refresh=true",
                "{ \"data\" : \"e1xydGYxXGFuc2kNCkxvcmVtIGlwc3VtIGRvbG9yIHNpdCBhbWV0DQpccGFyIH0=\" }");
        
        
        // get the user and observe the auto generated fields/values
        Map result = client.get("/attachment/email/1", HashMap.class);
        Assert.assertEquals(true, result.get("found"));
        
        Map source = (Map)result.get("_source");
        Map attachment = (Map)source.get("attachment");
        Assert.assertEquals("ro", attachment.get("language"));
        Assert.assertEquals("Lorem ipsum dolor sit amet", attachment.get("content"));
    }
    
}
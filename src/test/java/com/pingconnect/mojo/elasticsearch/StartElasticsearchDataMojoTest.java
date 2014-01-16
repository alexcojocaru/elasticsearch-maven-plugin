package com.pingconnect.mojo.elasticsearch;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

/**
 * @author alexcojocaru
 *
 */
public class StartElasticsearchDataMojoTest extends AbstractMojoTestCase
{

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        
        ElasticSearchNode.stop();
    }
    
    public void testMojoLookup() throws Exception
    {
        File testPom = new File(getBasedir(), "src/test/resources/goals/start/pom.xml");
        
        StartElasticsearchNodeMojo mojo = (StartElasticsearchNodeMojo)lookupMojo("start", testPom);
 
        assertNotNull(mojo);
    }
    
    public void testMojoExecution() throws Exception
    {
        File testPom = new File(getBasedir(), "src/test/resources/goals/start/pom.xml");
        
        StartElasticsearchNodeMojo mojo = (StartElasticsearchNodeMojo)lookupMojo("start", testPom);
 
        assertNotNull(mojo);
        
        mojo.execute();
        
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet("http://localhost:" + ElasticSearchNode.getHttpPort());
        HttpResponse response = client.execute(get);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

}

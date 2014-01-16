package com.pingconnect.mojo.elasticsearch;

import java.io.File;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

/**
 * @author alexcojocaru
 *
 */
public class StopElasticsearchDataMojoTest extends AbstractMojoTestCase
{

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        String dataPath = new File("target/test-harness/elasticsearch-data").getAbsolutePath();
        ElasticSearchNode.start(dataPath);
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();

        ElasticSearchNode.stop();
    }
    
    public void testMojoLookup() throws Exception
    {
        File testPom = new File(getBasedir(), "src/test/resources/goals/stop/pom.xml");
        
        StopElasticsearchNodeMojo mojo = (StopElasticsearchNodeMojo)lookupMojo("stop", testPom);
 
        assertNotNull(mojo);
    }
    
    public void testMojoExecution() throws Exception
    {
        File testPom = new File(getBasedir(), "src/test/resources/goals/stop/pom.xml");
        StopElasticsearchNodeMojo mojo = (StopElasticsearchNodeMojo)lookupMojo("stop", testPom);
        assertNotNull(mojo);
        mojo.execute();
        
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet("http://localhost:" + ElasticSearchNode.getHttpPort());
        try
        {
            client.execute(get);
            
            fail("The ES cluster should have been down by now");
        }
        catch (HttpHostConnectException expected)
        {
        }
    }

}

package com.github.alexcojocaru.mojo.elasticsearch;

import java.io.File;
import java.util.HashMap;

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

    private ElasticsearchNode elasticsearchNode;

    private StopElasticsearchNodeMojo mojo;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        String dataPath = new File("target/test-harness/elasticsearch-data").getAbsolutePath();
        elasticsearchNode = ElasticsearchNode.start(dataPath);

        //Configure mojo with context
        File testPom = new File(getBasedir(), "src/test/resources/goals/stop/pom.xml");
        mojo = (StopElasticsearchNodeMojo)lookupMojo("stop", testPom);
        mojo.setPluginContext(new HashMap());
        mojo.getPluginContext().put("test", elasticsearchNode);
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        if (elasticsearchNode != null && !elasticsearchNode.isClosed())
        {
            elasticsearchNode.stop();
        }
    }
    
    public void testMojoLookup() throws Exception
    {
        assertNotNull(mojo);
    }
    
    public void testMojoExecution() throws Exception
    {
        assertNotNull(mojo);
        mojo.execute();
        
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet("http://localhost:" + elasticsearchNode.getHttpPort());
        try
        {
            client.execute(get);
            
            fail("The ES cluster should have been down by now");
        }
        catch (HttpHostConnectException expected)
        {
        }

        assertTrue(elasticsearchNode.isClosed());
    }

}

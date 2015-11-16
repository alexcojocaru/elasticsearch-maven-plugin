package com.github.alexcojocaru.mojo.elasticsearch;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;
import java.util.HashMap;

/**
 * @author alexcojocaru
 *
 */
public class StartElasticsearchDataMojoTest extends AbstractMojoTestCase
{

    private StartElasticsearchNodeMojo mojo;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        File testPom = new File(getBasedir(), "src/test/resources/goals/start/pom.xml");
        mojo = (StartElasticsearchNodeMojo) lookupMojo("start", testPom);
        mojo.setPluginContext(new HashMap());
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        if (mojo != null && mojo.getNode() != null)
        {
            mojo.getNode().stop();
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
        HttpGet get = new HttpGet("http://localhost:" + mojo.getNode().getHttpPort());
        HttpResponse response = client.execute(get);
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(true, mojo.autoCreateIndex);
    }

}

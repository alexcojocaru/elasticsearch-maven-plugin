package com.github.alexcojocaru.mojo.elasticsearch;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import com.github.alexcojocaru.mojo.elasticsearch.NetUtil.ElasticsearchPort;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author alexcojocaru
 *
 */
public class StartElasticsearchDataMojoTest extends AbstractMojoTestCase
{

    private StartElasticsearchNodeMojo mojo;
    private HttpClient httpClient;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        File testPom = new File(getBasedir(), "src/test/resources/goals/start/pom.xml");

        Map<ElasticsearchPort, Integer> esPorts = NetUtil.findOpenPortsForElasticsearch();

        mojo = (StartElasticsearchNodeMojo) lookupMojo("start", testPom);
        mojo.setPluginContext(new HashMap());
        
        // I cannot find another way of setting the two required propperties at run time.
        mojo.httpPort = esPorts.get(ElasticsearchPort.HTTP);
        mojo.tcpPort = esPorts.get(ElasticsearchPort.TCP);

        httpClient = HttpClientBuilder.create().build();
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();

        stopNode();
    }

    private void stopNode() throws Exception
    {
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

        HttpGet get = new HttpGet(getUri());
        HttpResponse response = httpClient.execute(get);
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(true, mojo.autoCreateIndex);
    }

    public void testMojoExecutionIsSkipped() throws Exception
    {
        mojo.skip = true;

        assertNotNull(mojo);
        mojo.execute();

        assertNull(mojo.getNode());
    }

    private String getUri() throws Exception
    {
        return "http://localhost:" + mojo.getNode().getHttpPort();
    }

    public void testKeepData() throws Exception
    {
        assertNotNull(mojo);

        mojo.execute();

        final String indexName = "myindex";

        // succeeds
        StatusLine statusLine = createIndex(indexName);
        assertEquals(statusLine.getReasonPhrase(), 2, statusLine.getStatusCode() / 100);

        // fails because exists
        Thread.sleep(500);
        statusLine = createIndex(indexName);
        assertEquals(statusLine.getReasonPhrase(), 4, statusLine.getStatusCode() / 100);

        stopNode();
        setUp();
        Thread.sleep(500);
        mojo.execute();

        // succeeds because deleted and newly created
        statusLine = createIndex(indexName);
        assertEquals(statusLine.getReasonPhrase(), 2, statusLine.getStatusCode() / 100);

        stopNode();
        setUp();
        Thread.sleep(500);
        mojo.keepData = true;
        mojo.execute();

        // fails because existed from previous
        statusLine = createIndex(indexName);
        assertEquals(statusLine.getReasonPhrase(), 4, statusLine.getStatusCode() / 100);

    }

    private StatusLine createIndex(final String indexName) throws Exception
    {
        final String indexUri = getUri() + "/" + indexName;
        final HttpResponse response = httpClient.execute(new HttpPut(indexUri));
        return response.getStatusLine();
    }

}

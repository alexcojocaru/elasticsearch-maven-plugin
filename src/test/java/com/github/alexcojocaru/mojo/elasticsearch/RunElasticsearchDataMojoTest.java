package com.github.alexcojocaru.mojo.elasticsearch;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author gfernandes
 */
public class RunElasticsearchDataMojoTest extends AbstractMojoTestCase
{

    private ElasticsearchNode elasticsearchNode;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        if (elasticsearchNode != null)
        {
            elasticsearchNode.stop();
        }
        
    }
    
    public void testMojoLookup() throws Exception
    {
        File testPom = new File(getBasedir(), "src/test/resources/goals/run/pom.xml");

        RunElasticsearchNodeMojo mojo = (RunElasticsearchNodeMojo)lookupMojo("run", testPom);
 
        assertNotNull(mojo);
    }

    /**
     * Test that verifies Run goal.
     * It runs asynchronously because the goal blocks the mojo execution so
     * the test has to invoked it in a separate thread, wait a few moments until it is started
     * and do the assertions, finally on the final block stops (by interrupting the thread) ES.
     * @throws Exception
     */
    public void testMojoExecution() throws Exception
    {
        File testPom = new File(getBasedir(), "src/test/resources/goals/run/pom.xml");

        final RunElasticsearchNodeMojo mojo = (RunElasticsearchNodeMojo)lookupMojo("run", testPom);
        mojo.setPluginContext(new HashMap());

        final AtomicReference<Exception> internalRunThreadException = new AtomicReference<Exception>();

        assertNotNull(mojo);
        
        Thread toRun = new Thread(new Runnable() {
            public void run() {
                try {
                    mojo.execute();
                } catch (MojoExecutionException e) {
                    e.printStackTrace();
                    internalRunThreadException.set(e);
                }
            }
        });

        toRun.start();

        try {
            Thread.sleep(3000);
            elasticsearchNode = mojo.getNode();

            // Asserts!
            assertNull("MojoExecute threw an exception", internalRunThreadException.get());

            HttpClient client = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet("http://localhost:" + elasticsearchNode.getHttpPort());
            HttpResponse response = client.execute(get);
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
        catch (Exception e ) {
            fail(e.getMessage());
        }
        finally {
            try {
                toRun.interrupt();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}

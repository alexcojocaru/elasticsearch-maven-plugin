package com.github.alexcojocaru.mojo.elasticsearch;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Ignore;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author gfernandes
 */
public class RunElasticsearchDataMojoTest extends AbstractMojoTestCase
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
        File testPom = new File(getBasedir(), "src/test/resources/goals/run/pom.xml");

        RunElasticSearchNodeMojo mojo = (RunElasticSearchNodeMojo)lookupMojo("run", testPom);
 
        assertNotNull(mojo);
    }
    
    public void testMojoExecution() throws Exception
    {
        File testPom = new File(getBasedir(), "src/test/resources/goals/run/pom.xml");

        final RunElasticSearchNodeMojo mojo = (RunElasticSearchNodeMojo)lookupMojo("run", testPom);

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

            // Asserts!
            assertNull("MojoExecute threw an exception", internalRunThreadException.get());

            HttpClient client = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet("http://localhost:" + ElasticSearchNode.getHttpPort());
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

package com.github.alexcojocaru.mojo.elasticsearch;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import com.github.alexcojocaru.mojo.elasticsearch.NetUtil.ElasticsearchPort;
import com.jayway.awaitility.Awaitility;

/**
 * @author gfernandes
 */
public class RunElasticsearchDataMojoTest extends AbstractMojoTestCase
{
    private RunElasticsearchNodeMojo mojo;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        File testPom = new File(getBasedir(), "src/test/resources/goals/run/pom.xml");

        Map<ElasticsearchPort, Integer> esPorts = NetUtil.findOpenPortsForElasticsearch();

        mojo = (RunElasticsearchNodeMojo)lookupMojo("run", testPom);
        mojo.setPluginContext(new HashMap());
        
        // I cannot find another way of setting the two required propperties at run time.
        mojo.httpPort = esPorts.get(ElasticsearchPort.HTTP);
        mojo.tcpPort = esPorts.get(ElasticsearchPort.TCP);
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

    /**
     * Test that verifies Run goal.
     * It runs asynchronously because the goal blocks the mojo execution so
     * the test has to invoked it in a separate thread, wait a few moments until it is started
     * and do the assertions, finally on the final block stops (by interrupting the thread) ES.
     * @throws Exception
     */
    public void testMojoExecution() throws Exception
    {
        final AtomicReference<Exception> internalRunThreadException = new AtomicReference<Exception>();

        assertNotNull(mojo);
        
        Thread toRun = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    mojo.execute();
                }
                catch (MojoExecutionException e)
                {
                    e.printStackTrace();
                    internalRunThreadException.set(e);
                }
            }
        });

        toRun.start();

        try
        {
            // verify that the node start did not fail
            assertNull("MojoExecute threw an exception", internalRunThreadException.get());

            final HttpClient client = HttpClientBuilder.create().build();
            
            final HttpGet get = new HttpGet("http://localhost:" + mojo.httpPort);

            final int connectionTimeout = 100; // millis
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionTimeout)
                    .setConnectTimeout(connectionTimeout)
                    .setSocketTimeout(connectionTimeout)
                    .build();
            get.setConfig(requestConfig);

            Awaitility
                    .with().pollInterval(200, TimeUnit.MILLISECONDS)
                    .and()
                    .with().pollDelay(200, TimeUnit.MILLISECONDS)
                    .atMost(4000, TimeUnit.MILLISECONDS)
                    .await()
                    .until(new Callable<Boolean>()
                    {
                        @Override
                        public Boolean call() throws Exception
                        {
                            try
                            {
                                HttpResponse response = client.execute(get);
                                return response.getStatusLine().getStatusCode() == 200;
                            }
                            catch (Exception e)
                            {
                                // lets just assume the exception is due to the node starting
                                // and not log the exception
                                return false;
                            }
                        }
                    });
        }
        finally
        {
            try
            {
                toRun.interrupt();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}

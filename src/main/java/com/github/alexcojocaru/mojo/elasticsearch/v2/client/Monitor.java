package com.github.alexcojocaru.mojo.elasticsearch.v2.client;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.maven.plugin.logging.Log;
import org.awaitility.Awaitility;

/**
 * A monitor on an Elasticsearch instance.
 * 
 * @author Alex Cojocaru
 *
 */
public class Monitor
{
    private final ElasticsearchClient client;
    private final Log log;
    
    public Monitor(ElasticsearchClient client, Log log)
    {
        this.client = client;
        this.log = log;
    }
    
    public void waitToStart(final String clusterName, int timeout)
    {
        log.debug(String.format("Waiting  up to %ds for Elasticsearch to start ...", timeout));
        Awaitility.await()
                .atMost(timeout, TimeUnit.SECONDS)
                .pollDelay(1, TimeUnit.SECONDS)
                .pollInterval(1,  TimeUnit.SECONDS)
                .until(new Callable<Boolean>()
                        {
                            @Override
                            public Boolean call() throws Exception
                            {
                                return isRunning(clusterName);
                            }
                        }
                );
        log.debug("Elasticsearch has started");
    }
    
    public boolean isRunning(String clusterName)
    {
        boolean result;
        try
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = client.get("/", Map.class);
            result = clusterName.equals(response.get("cluster_name"));
        }
        catch (ElasticsearchClientException e)
        {
            // failure is allowed
            result = false;
        }
        
        return result;
    }
}

package com.github.alexcojocaru.mojo.elasticsearch.v2.client;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.maven.plugin.logging.Log;
import org.awaitility.Awaitility;
import org.mockito.Mockito;

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
    
    public void waitToStart(final String baseDir, final String clusterName, int timeout)
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
                                return isProcessRunning(baseDir)
                                        && isClusterRunning(clusterName, client);
                            }
                        }
                );
        log.debug("Elasticsearch has started");
    }
    
    /**
     * Check whether the PID file created by the ES process exists or not.
     * @param baseDir the ES base directory
     * @return true if the process is running, false otherwise
     */
    public static boolean isProcessRunning(String baseDir)
    {
        File pidFile = new File(baseDir, "pid");
        boolean exists = pidFile.isFile();
        return exists;
    }
    
    /**
     * Check whether the cluster with the given name exists in the ES referenced by the client.
     * @param clusterName the ES cluster name
     * @param client the ES client to use to connect to ES
     * @return true if the cluster is running, false otherwise
     */
    public static boolean isClusterRunning(String clusterName, ElasticsearchClient client)
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
    
    /**
     * Check whether the cluster with the given name exists in the ES running on the given port.
     * @param clusterName the ES cluster name
     * @param httpPort the HTTP port to connect to ES
     * @return true if the cluster is running, false otherwise
     */
    public static boolean isClusterRunning(String clusterName, int httpPort)
    {
        Log log = Mockito.mock(Log.class);
        ElasticsearchClient client = new ElasticsearchClient(log, "localhost", httpPort);
        return isClusterRunning(clusterName, client);
    }
}

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

    public void waitToStartInstance(final String baseDir, final String clusterName, int timeout)
    {
        log.debug(String.format(
                "Waiting up to %ds for the Elasticsearch instance to start ...",
                timeout));
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
                                        && isInstanceRunning(clusterName);
                            }
                        }
                );
        log.info("The Elasticsearch instance has started");
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
     * Check whether the cluster with the given name exists in the current ES instance.
     * @param clusterName the ES cluster name
     * @return true if the instance is running, false otherwise
     */
    public boolean isInstanceRunning(String clusterName)
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
     * <br><br>
     * This is an expensive method, for it initializes a new ES client.
     * @param clusterName the ES cluster name
     * @param httpPort the HTTP port to connect to ES
     * @return true if the instance is running, false otherwise
     */
    public static boolean isInstanceRunning(String clusterName, int httpPort)
    {
        Log log = Mockito.mock(Log.class);

        try (ElasticsearchClient client = new ElasticsearchClient.Builder()
                .withLog(log)
                .withHostname("localhost")
                .withPort(httpPort)
                .withSocketTimeout(5000)
                .build())
        {
            return new Monitor(client, log).isInstanceRunning(clusterName);
        }
    }

    /**
     * Wait until the cluster has fully started (ie. all nodes have joined).
     * @param clusterName the ES cluster name
     * @param nodesCount the number of nodes in the cluster
     * @param timeout how many seconds to wait
     */
    public void waitToStartCluster(final String clusterName, int nodesCount, int timeout)
    {
        log.debug(String.format(
                "Waiting up to %ds for the Elasticsearch cluster to start ...",
                timeout));
        Awaitility.await()
                .atMost(timeout, TimeUnit.SECONDS)
                .pollDelay(1, TimeUnit.SECONDS)
                .pollInterval(1,  TimeUnit.SECONDS)
                .until(new Callable<Boolean>()
                        {
                            @Override
                            public Boolean call() throws Exception
                            {
                                return isClusterRunning(clusterName, nodesCount, client);
                            }
                        }
                );
        log.info("The Elasticsearch cluster has started");
    }

    /**
     * Verify that the cluster name and the number of nodes in the cluster,
     * as reported by the ES node, is as expected.
     * @param clusterName the ES cluster name
     * @param instanceCount the number of ES nodes in the cluster
     * @param client the ES client to use to connect to ES
     * @return true if the cluster is running, false otherwise
     */
    public static boolean isClusterRunning(String clusterName,
            int instanceCount,
            ElasticsearchClient client)
    {
        boolean result;
        try
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = client.get("/_nodes", Map.class);
            result = clusterName.equals(response.get("cluster_name"));

            @SuppressWarnings("unchecked")
            Map<String, Object> nodesInfo = (Map<String, Object>)response.get("_nodes");
            result &= instanceCount == (int)(nodesInfo.get("successful"));
        }
        catch (ElasticsearchClientException e)
        {
            // failure is allowed
            result = false;
        }

        return result;
    }

    /**
     * Verify that the cluster name and the number of nodes in the cluster,
     * as reported by the ES node, is as expected.
     * @param clusterName the ES cluster name
     * @param instanceCount the number of ES nodes in the cluster
     * @param httpPort the HTTP port to connect to ES
     * @return true if the cluster is running, false otherwise
     */
    public static boolean isClusterRunning(String clusterName, int instanceCount, int httpPort)
    {
        Log log = Mockito.mock(Log.class);

        try (ElasticsearchClient client = new ElasticsearchClient.Builder()
                .withLog(log)
                .withHostname("localhost")
                .withPort(httpPort)
                .withSocketTimeout(5000)
                .build())
        {
            return isClusterRunning(clusterName, instanceCount, client);
        }
    }
}

package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClient;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.Monitor;

/**
 * Wait until the ES cluster is up and running.
 * 
 * @author Alex Cojocaru
 */
public class WaitToStartClusterStep
        implements ClusterStep
{
    @Override
    public void execute(ClusterConfiguration config)
    {
        try (ElasticsearchClient client = new ElasticsearchClient.Builder()
                .withInstanceConfiguration(config.getInstanceConfigurationList().get(0))
                .withHostname("localhost")
                .build())
        {
            Monitor monitor = new Monitor(client, config.getLog());
            monitor.waitToStartCluster(
                    config.getClusterName(),
                    config.getInstanceConfigurationList().size(),
                    config.getStartupTimeout());
        }
    }
}

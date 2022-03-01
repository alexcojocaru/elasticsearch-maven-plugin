package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClient;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.Monitor;

/**
 * Wait until the ES instance is up and running.
 * 
 * @author Alex Cojocaru
 */
public class WaitToStartInstanceStep
        implements InstanceStep
{
    @Override
    public void execute(InstanceConfiguration config)
    {
        int timeout = config.getStartupTimeout();

        try (ElasticsearchClient client = new ElasticsearchClient.Builder()
                .withInstanceConfiguration(config)
                .withHostname("localhost")
                .build())
        {
            Monitor monitor = new Monitor(client, config.getClusterConfiguration().getLog());
            monitor.waitToStartInstance(
                    config.getBaseDir(),
                    config.getClusterConfiguration().getClusterName(),
                    timeout);
        }
    }
}

package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClient;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.Monitor;

/**
 * Wait until the ES instance is up and running.
 * 
 * @author Alex Cojocaru
 */
public class WaitToStartStep
        implements InstanceStep
{

    @Override
    public void execute(InstanceConfiguration config)
    {
        int httpPort = config.getHttpPort();
        int timeout = config.getClusterConfiguration().getTimeout();

        ElasticsearchClient client = new ElasticsearchClient("localhost", httpPort);
        Monitor monitor = new Monitor(client, config.getClusterConfiguration().getLog());
        monitor.waitToStart(timeout);
    }

}

package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceContext;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClient;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.Monitor;

/**
 * Wait until the ES instance is up and running.
 * 
 * @author Alex Cojocaru
 */
public class WaitToStartStep
        implements Step
{

    @Override
    public void execute(InstanceContext context)
    {
        int httpPort = context.getConfiguration().getHttpPort();
        int timeout = context.getConfiguration().getTimeout();

        ElasticsearchClient client = new ElasticsearchClient("localhost", httpPort);
        Monitor monitor = new Monitor(client, context.getLog());
        monitor.waitToStart(timeout);
    }

}

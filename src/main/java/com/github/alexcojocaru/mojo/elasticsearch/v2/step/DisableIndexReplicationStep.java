package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClient;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClientException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.VersionUtil;

/**
 * Disable the replication of indexes / shards if the cluster has a single node.
 * <br><br>
 * By default, index shards are replicated to at least one additional node.
 * On single-node clusters, since there are no nodes to replicate them to,
 * these shards will be 'unassigned', hence the cluster status will be 'yellow'.
 * Disabling the index replication will make the cluster be 'green'.
 * 
 * @author Alex Cojocaru
 */
public class DisableIndexReplicationStep
        implements ClusterStep
{

    @Override
    public void execute(ClusterConfiguration config)
    {
        if (config.getInstanceConfigurationList().size() != 1) {
            return;
        }

        try (ElasticsearchClient client = new ElasticsearchClient.Builder()
                .withInstanceConfiguration(config.getInstanceConfigurationList().get(0))
                .withHostname("localhost")
                .build())
        {
            if (VersionUtil.isEqualOrGreater_8_0_0(config.getVersion()))
            {
                String content = "{"
                        + " \"index_patterns\" : [\"*\"],"
                        // Some random priority (still as high as possible),
                        // to avoid clashing with existing templates
                        + " \"priority\" : " + (Integer.MAX_VALUE - 127) + ","
                        + " \"template\": { \"settings\" : { \"number_of_replicas\": 0 } }"
                        + " }";
                client.put("/_index_template/default_template", content);
            }
            else
            {
                String content = "{"
                        + " \"template\": \"*\","
                        + " \"index_patterns\": [\"*\"],"
                        + " \"settings\": { \"number_of_replicas\": 0 }"
                        + " }";
                client.put("/_template/default_template", content);
            }
        }
        catch (ElasticsearchClientException e)
        {
            throw new ElasticsearchSetupException("Cannot disable index replication", e);
        }
    }
}

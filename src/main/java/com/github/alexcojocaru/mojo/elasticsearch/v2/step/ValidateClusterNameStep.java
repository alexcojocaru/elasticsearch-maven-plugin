package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;

/**
 * Validate that the provided cluster name is not empty and is alphanumeric.
 * 
 * @author Alex Cojocaru
 */
public class ValidateClusterNameStep
        implements ClusterStep
{

    @Override
    public void execute(ClusterConfiguration config)
    {
        String clusterName = config.getClusterName();
        
        if (clusterName == null)
        {
            throw new ElasticsearchSetupException(String.format(
                    "Please provide a cluster name."));
        }

        if (clusterName.matches("[a-zA-Z0-9.-]+") == false)
        {
            throw new ElasticsearchSetupException(String.format(
                    "elasticsearch-maven-plugin supports only alphanumeric with dots and dashes cluster names. You configured: %s.",
                    clusterName));
        }
    }

}

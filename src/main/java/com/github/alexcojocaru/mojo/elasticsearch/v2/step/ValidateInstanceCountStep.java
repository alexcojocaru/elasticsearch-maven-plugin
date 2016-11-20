package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;

/**
 * Validate that the provided number of Elasticserach instances is greater than 0.
 * 
 * @author Alex Cojocaru
 */
public class ValidateInstanceCountStep
        implements ClusterStep
{

    @Override
    public void execute(ClusterConfiguration config)
    {
        int instanceCount = config.getInstanceConfigurationList().size();
        
        if (instanceCount < 1)
        {
            throw new ElasticsearchSetupException(String.format(
                    "The number of instances should not be smaller than 1; you configured: %d",
                    instanceCount));
        }
    }

}

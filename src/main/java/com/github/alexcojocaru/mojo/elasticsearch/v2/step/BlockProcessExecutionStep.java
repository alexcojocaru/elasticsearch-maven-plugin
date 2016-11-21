package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ExecutionLock;

/**
 * Block the current process execution if the setAwait flag is set on the cluster configuration.
 * 
 * @author Alex Cojocaru
 */
public class BlockProcessExecutionStep
        implements ClusterStep
{

    @Override
    public void execute(ClusterConfiguration config)
    {
        if (config.isSetAwait())
        {
            new ExecutionLock(config.getLog()).lock();
        }
    }

}

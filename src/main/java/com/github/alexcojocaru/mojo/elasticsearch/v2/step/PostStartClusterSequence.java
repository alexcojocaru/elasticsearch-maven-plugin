package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

/**
 * Combines all steps to execute after the ES instance has started.
 * 
 * @author Alex Cojocaru
 */
public class PostStartClusterSequence
        extends DefaultClusterStepSequence
{

    public PostStartClusterSequence()
    {
        add(new WaitToStartClusterStep());
        add(new DisableIndexReplicationStep());
        add(new BootstrapClusterStep());
        add(new BlockProcessExecutionStep());
    }
}
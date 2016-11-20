package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import java.util.LinkedList;
import java.util.List;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;

/**
 * Implementation of a {@link ClusterStepSequence}.
 * 
 * @author Alex Cojocaru
 */
public class DefaultClusterStepSequence
        implements ClusterStepSequence
{
    protected List<ClusterStep> sequence = new LinkedList<ClusterStep>();

    @Override
    public void execute(ClusterConfiguration config)
    {
        sequence.forEach(step -> step.execute(config));
    }

    @Override
    public void add(ClusterStep step)
    {
        this.sequence.add(step);
    }

}

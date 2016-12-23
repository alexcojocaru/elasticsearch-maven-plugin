package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import java.util.LinkedList;
import java.util.List;

import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;

/**
 * Implementation of a {@link InstanceStepSequence}.
 * 
 * @author Alex Cojocaru
 */
public class DefaultInstanceStepSequence
        implements InstanceStepSequence
{
    protected List<InstanceStep> sequence = new LinkedList<InstanceStep>();

    @Override
    public void execute(InstanceConfiguration config)
    {
        sequence.forEach(step -> step.execute(config));
    }

    @Override
    public void add(InstanceStep step)
    {
        this.sequence.add(step);
    }

}

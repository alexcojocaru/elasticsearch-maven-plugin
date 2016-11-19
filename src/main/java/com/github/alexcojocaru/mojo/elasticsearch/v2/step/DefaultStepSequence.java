package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import java.util.LinkedList;
import java.util.List;

import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceContext;

/**
 * Implementation of a {@link StepSequence}.
 * 
 * @author Alex Cojocaru
 */
public class DefaultStepSequence
        implements StepSequence
{

    protected List<Step> sequence = new LinkedList<Step>();

    @Override
    public void execute(InstanceContext context)
    {
        sequence.forEach(step -> step.execute(context));
    }

    @Override
    public void add(Step step)
    {
        this.sequence.add(step);
    }

}

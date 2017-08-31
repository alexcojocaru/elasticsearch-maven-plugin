package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

/**
 * Combines all steps to execute after the ES instance has started.
 * 
 * @author Alex Cojocaru
 */
public class PostStartInstanceSequence
        extends DefaultInstanceStepSequence
{

    public PostStartInstanceSequence()
    {
        add(new WaitToStartInstanceStep());
    }
}
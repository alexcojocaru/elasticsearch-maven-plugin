package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

/**
 * Combines all steps needed for setting up a forked-elasticsearch with all configured elements.
 * 
 * @author Alex Cojocaru
 */
public class ForkedSetupSequence
        extends DefaultStepSequence
{

    public ForkedSetupSequence()
    {
        add(new ResolveElasticsearchStep());
    }
}
package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

/**
 * Combines all steps needed for setting up a forked-elasticsearch with all configured elements.
 * 
 * @author Alex Cojocaru
 */
public class InstanceSetupSequence
        extends DefaultInstanceStepSequence
{

    public InstanceSetupSequence()
    {
        add (new RemoveExistingDataStep());

        add(new ResolveElasticsearchStep());
        
        add(new RemovePluginsStep());
        
        add(new InstallPluginsStep());
    }
}
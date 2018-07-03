package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

/**
 * Combines all steps to execute before attempting to start the ES instances.
 * 
 * @author Alex Cojocaru
 */
public class PreStartClusterSequence
        extends DefaultClusterStepSequence
{

    public PreStartClusterSequence()
    {
        add(new ValidateInstanceCountStep());
        add(new ValidateBaseDirectoryStep());
        add(new ValidateFlavourStep());
        add(new ValidateVersionStep());
        add(new ValidateClusterNameStep());
        add(new ValidateUniquePortsStep());
        add(new ValidatePortsStep());
        add(new ValidatePathConfStep());
    }
}
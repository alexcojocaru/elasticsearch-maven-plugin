package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;

/**
 * Validate that the provided Elasticsearch version is valid (ie. 5.0.0+).
 * 
 * @author Alex Cojocaru
 */
public class ValidateFlavourStep
        implements ClusterStep
{

    @Override
    public void execute(ClusterConfiguration config)
    {
        String flavour = config.getFlavour();
        String version = config.getVersion();
        
        config.getLog().debug(
                "Checking flavour '" + flavour + "' against version '" + version + "'");
        
        // nothing to check;
        // for ES versions less than 6.3.0 and greater than 7.11.0, this property is ignored
        // for ES versions 6.3.0 to 7.10.x, we allow pretty much anything
    }

}

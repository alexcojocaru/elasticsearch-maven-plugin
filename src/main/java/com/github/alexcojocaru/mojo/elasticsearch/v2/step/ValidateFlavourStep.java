package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import org.apache.commons.lang3.StringUtils;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.VersionUtil;

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
        
        if (StringUtils.isNotBlank(flavour) && VersionUtil.isBetween_5_0_0_and_6_2_x(version))
        {
            throw new ElasticsearchSetupException(String.format(
                    "The flavour property is not supported for Elasticsearch [5.0.0 - 6.3.0)."));
        }
    }

}

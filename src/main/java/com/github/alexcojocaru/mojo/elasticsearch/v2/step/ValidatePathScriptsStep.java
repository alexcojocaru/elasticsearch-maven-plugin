package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;

/**
 * Validate that the provided pathScripts, if any, points to an existing directory.
 * 
 * @author Alex Cojocaru
 */
public class ValidatePathScriptsStep
        implements ClusterStep
{

    @Override
    public void execute(ClusterConfiguration config)
    {
        String pathScripts = config.getPathScripts();
        
        if (StringUtils.isNotBlank(pathScripts) && new File(pathScripts).isDirectory() == false)
        {
            throw new ElasticsearchSetupException(String.format(
                    "The value of the 'pathScripts' parameter ('%1$s') must be the absolute path"
                    + " (or relative to the maven project) of an existing directory.", pathScripts));
        }
    }

}

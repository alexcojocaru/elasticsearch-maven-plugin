package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;

/**
 * Validate that the provided pathConf, if any, points to an existing directory.
 *
 * @author Alex Cojocaru
 */
public class ValidatePathConfStep
        implements ClusterStep
{

    @Override
    public void execute(ClusterConfiguration config)
    {
        String pathConf = config.getPathConf();

        if (StringUtils.isNotBlank(pathConf) && new File(pathConf).isDirectory() == false)
        {
            throw new ElasticsearchSetupException(String.format(
                    "The value of the 'pathConf' parameter ('%1$s') must be the absolute path"
                    + " (or relative to the maven project) of an existing directory.", pathConf));
        }
    }

}

package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import java.io.File;

import org.apache.commons.lang3.Validate;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;

/**
 * Validate that the provided Elasticsearch base directory is valid.
 * 
 * @author Alex Cojocaru
 *
 */
public class ValidateBaseDirectoryStep
        implements ClusterStep
{

    @Override
    public void execute(ClusterConfiguration config)
    {
        String baseDir = config.getInstanceConfigurationList().get(0).getBaseDir();
        
        try
        {
            Validate.notBlank(baseDir);
            new File(baseDir).getCanonicalPath(); // this should catch erroneous paths
        }
        catch (Exception e)
        {
            throw new ElasticsearchSetupException(String.format(
                    "The value of the 'baseDir' parameter ('%1$s') is not a valid file path.",
                    baseDir));
        }
    }

}

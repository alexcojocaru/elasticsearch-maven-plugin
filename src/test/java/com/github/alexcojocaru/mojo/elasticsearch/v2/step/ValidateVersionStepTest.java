package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.PluginArtifactResolver;

/**
 * @author Alex Cojocaru
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidateVersionStepTest
{
    @Mock
    private PluginArtifactResolver artifactResolver;
    
    @Mock
    private Log log;
    
    /**
     * Test the version check with correct version
     */
    @Test
    public void testCheckVersionWithCorrectVersion()
    {
        String version = "5.1";
        ClusterConfiguration config = buildConfig(version);

        new ValidateVersionStep().execute(config);
    }

    /**
     * Test the version check with incorrect version
     */
    @Test(expected = ElasticsearchSetupException.class)
    public void testCheckVersionWithIncorrectVersion()
    {
        String version = "1.0.0";
        ClusterConfiguration config = buildConfig(version);

        new ValidateVersionStep().execute(config);
    }
    
    private ClusterConfiguration buildConfig(String version)
    {
        ClusterConfiguration config = new ClusterConfiguration.Builder()
                .withVersion(version)
                .build();
        
        return config;
    }
}

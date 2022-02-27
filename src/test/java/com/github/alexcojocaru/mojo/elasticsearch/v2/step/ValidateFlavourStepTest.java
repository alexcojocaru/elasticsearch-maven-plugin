package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.PluginArtifactResolver;

/**
 * @author Alex Cojocaru
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidateFlavourStepTest
{
    @Mock
    private PluginArtifactResolver artifactResolver;
    
    @Mock
    private Log log;
    
    /**
     * Test the flavour check with correct version
     */
    @Test
    public void testCheckFlavour()
    {
        new ValidateFlavourStep().execute(buildConfig("5.1.1", ""));
        new ValidateFlavourStep().execute(buildConfig("8.0.0", ""));

        new ValidateFlavourStep().execute(buildConfig("6.5.0", ""));
        new ValidateFlavourStep().execute(buildConfig("6.5.0", "flavour"));

        new ValidateFlavourStep().execute(buildConfig("7.0.0", ""));
        new ValidateFlavourStep().execute(buildConfig("7.0.0", "something"));
    }
    
    private ClusterConfiguration buildConfig(String version, String flavour)
    {
        ClusterConfiguration config = new ClusterConfiguration.Builder()
                .withVersion(version)
                .withFlavour(flavour)
                .withLog(log)
                .build();
        
        return config;
    }
}

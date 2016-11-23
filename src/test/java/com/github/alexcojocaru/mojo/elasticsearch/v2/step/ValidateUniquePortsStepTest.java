package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.PluginArtifactResolver;

/**
 * @author Alex Cojocaru
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidateUniquePortsStepTest
{
    @Mock
    private PluginArtifactResolver artifactResolver;
    
    @Mock
    private Log log;
    
    /**
     * Test with 2 instances; the http ports are duplicates, the transport ports are unique.
     */
    @Test(expected = ElasticsearchSetupException.class)
    public void testWithTwoInstancesWithDuplicateHttpPorts()
    {
        ClusterConfiguration config = buildConfig(2000, 2010, 2000, 2011);

        new ValidateUniquePortsStep().execute(config);
    }
    
    /**
     * Test with 2 instances; the http ports are unique, the transport ports are duplicates.
     */
    @Test(expected = ElasticsearchSetupException.class)
    public void testWithTwoInstancesWithDuplicateTransportPorts()
    {
        ClusterConfiguration config = buildConfig(2000, 2010, 2001, 2010);

        new ValidateUniquePortsStep().execute(config);
    }
    
    /**
     * Test with 2 instances; all ports are unique.
     */
    @Test
    public void testWithTwoInstancesWithUniquePorts()
    {
        ClusterConfiguration config = buildConfig(2000, 2010, 2001, 2011);

        new ValidateUniquePortsStep().execute(config);
    }
    
    /**
     * Test with a single instance with duplicate ports.
     */
    @Test(expected = ElasticsearchSetupException.class)
    public void testWithSingleInstanceWithDuplicatePorts()
    {
        ClusterConfiguration config = buildConfig(2000, 2000);

        new ValidateUniquePortsStep().execute(config);
    }
    
    /**
     * Test with a single instance with unique ports.
     */
    @Test
    public void testWithSingleInstanceWithUniquePorts()
    {
        ClusterConfiguration config = buildConfig(2000, 2010);

        new ValidateUniquePortsStep().execute(config);
    }
    
    private ClusterConfiguration buildConfig(int... ports)
    {
        ClusterConfiguration.Builder configBuilder = new ClusterConfiguration.Builder();
        
        for (int i = 0; i < ports.length;)
        {
            int httpPort = ports[i++];
            int transportPort = ports[i++];

            configBuilder.addInstanceConfiguration(new InstanceConfiguration.Builder()
                    .withHttpPort(httpPort)
                    .withTransportPort(transportPort)
                    .build());
        }
        
        ClusterConfiguration config = configBuilder.build();
        
        return config;
    }
}

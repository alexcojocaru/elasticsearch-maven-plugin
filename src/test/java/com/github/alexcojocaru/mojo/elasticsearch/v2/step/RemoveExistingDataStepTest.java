package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;

/**
 * @author Alex Cojocaru
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RemoveExistingDataStepTest {
    
    @Test
    public void testKeepExistingData() {
        InstanceConfiguration instanceConfig = mock(InstanceConfiguration.class);
        
        ClusterConfiguration clusterConfig = new ClusterConfiguration.Builder()
                .withKeepExistingData(true)
                .build();
        when(instanceConfig.getClusterConfiguration()).thenReturn(clusterConfig);
        
        new RemoveExistingDataStep().execute(instanceConfig);
        
        // a bit convoluted, but this check that getBaseDir()
        // was never called in order to build the data and logs directories.
        Mockito.verify(instanceConfig, never()).getBaseDir();
    }
    
    @Test
    public void testRemoveExistingData() {
        InstanceConfiguration instanceConfig = mock(InstanceConfiguration.class);
        when(instanceConfig.getBaseDir()).thenReturn(".");
        
        ClusterConfiguration clusterConfig = new ClusterConfiguration.Builder()
                .withKeepExistingData(false)
                .build();
        when(instanceConfig.getClusterConfiguration()).thenReturn(clusterConfig);
        
        new RemoveExistingDataStep().execute(instanceConfig);
        
        // a bit convoluted, but this check that getBaseDir()
        // was called in order to build the data and logs directories.
        Mockito.verify(instanceConfig).getBaseDir();
    }

}

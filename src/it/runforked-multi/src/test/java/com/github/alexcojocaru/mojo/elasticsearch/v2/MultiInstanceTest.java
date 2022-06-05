package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.alexcojocaru.mojo.elasticsearch.v2.client.Monitor;

/**
 * 
 * @author Alex Cojocaru
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class MultiInstanceTest extends ItBase
{
    
    @Test
    public void testClusterRunningOnPrimaryInstance()
    {
        boolean isRunning = Monitor.isClusterRunning(log, clusterName, instanceCount, client);
        Assert.assertTrue("The ES cluster should be running on the primary instance", isRunning);
    }
    
    @Test
    public void testClusterRunningOnSecondaryInstance()
    {
        int secondaryHttpPort = httpPort + 1;
        
        boolean isRunning = Monitor.isClusterRunning(log, clusterName, instanceCount, secondaryHttpPort);
        Assert.assertTrue("The ES cluster should be running on the secondary instance", isRunning);
    }
    
}
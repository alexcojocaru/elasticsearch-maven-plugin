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
public class SkipTest extends ItBase
{
    
    @Test
    public void testClusterNotRunning()
    {
        boolean isRunning = Monitor.isClusterRunning(log, clusterName, instanceCount, client);
        Assert.assertFalse("The ES cluster should not be running", isRunning);
    }
    
}
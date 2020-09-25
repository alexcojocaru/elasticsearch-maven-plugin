package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ForkedElasticsearchProcessDestroyerTest {

    @Mock
    private InstanceConfiguration config;
    @Mock
    private ClusterConfiguration clusterConfig;
    @Mock
    private Log log;
    @Mock
    private Process process;

    @Test
    public void testRemoveDoesNotThrowIllegalStateException() throws InterruptedException, ExecutionException, TimeoutException {

        when(config.getClusterConfiguration()).thenReturn(clusterConfig);
        when(clusterConfig.getLog()).thenReturn(log);
        when(process.isAlive()).thenReturn(true);

        final ForkedElasticsearchProcessDestroyer destroyer = new ForkedElasticsearchProcessDestroyer(config);
        assertEquals("No process added", 0, destroyer.size());

        destroyer.add(process);
        assertEquals("One process added", 1, destroyer.size());

        destroyer.remove(process);
        assertEquals("One process removed, but still alive", 1, destroyer.size());

        destroyer.run();
        assertEquals("Alive process set to null", 0, destroyer.size());
    }

}

package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Alex Cojocaru
 */
@RunWith(MockitoJUnitRunner.class)
public class PreConditionsTest
{
    @Mock
    private Log log;

    /**
     * Test the version check with correct version
     */
    @Test
    public void testCheckVersionWithCorrectVersion()
    {
        String version = "5.1";

        PreConditions.checkConfiguredElasticsearchVersion(log, version);

        Mockito.verify(log, Mockito.times(0)).error(Mockito.anyString());
    }

    /**
     * Test the version check with correct version
     */
    @Test(expected = ElasticsearchSetupException.class)
    public void testCheckVersionWithIncorrectVersion()
    {
        String version = "1.0.0";

        PreConditions.checkConfiguredElasticsearchVersion(log, version);
    }

}

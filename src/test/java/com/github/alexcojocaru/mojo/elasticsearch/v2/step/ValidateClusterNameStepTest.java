package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ValidateClusterNameStepTest {
    private ValidateClusterNameStep validateClusterNameStep;
    ClusterConfiguration.Builder configBuilder;

    @Before
    public void setUp() {
        validateClusterNameStep = new ValidateClusterNameStep();
        configBuilder = new ClusterConfiguration.Builder();
    }

    @After
    public void tearDown() {
        validateClusterNameStep = null;
        configBuilder = null;
    }

    @Test
    public void TestWithClusterNameContainingDash() {
        ClusterConfiguration clusterConfiguration = configBuilder.withClusterName("ONE-TWO-THREE")
                                                                 .build();
        validateClusterNameStep.execute(clusterConfiguration);
    }

    @Test
    public void TestWithClusterNameContainingDot() {
        ClusterConfiguration clusterConfiguration = configBuilder.withClusterName("ONE.TWO.THREE")
                                                                 .build();
        validateClusterNameStep.execute(clusterConfiguration);
    }

    @Test(expected = ElasticsearchSetupException.class)
    public void TestWithClusterNameContainingQuote() {
        ClusterConfiguration clusterConfiguration = configBuilder.withClusterName("ONE'TWO'THREE")
                                                                 .build();
        validateClusterNameStep.execute(clusterConfiguration);
    }
}
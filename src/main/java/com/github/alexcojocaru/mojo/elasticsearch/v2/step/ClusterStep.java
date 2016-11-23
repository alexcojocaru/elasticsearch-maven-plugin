package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;

/**
 * Defines a small simple Step/Task/Unit of Work for an Elasticsearch cluster.
 * 
 * @author Alex Cojocaru
 */
public interface ClusterStep
{
    void execute(ClusterConfiguration config);
}

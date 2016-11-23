package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;

/**
 * Defines a small simple Step/Task/Unit of Work for an Elasticsearch instance.
 * 
 * @author Alex Cojocaru
 */
public interface InstanceStep
{
    void execute(InstanceConfiguration config);
}

package com.github.alexcojocaru.mojo.elasticsearch;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which stops the local Elasticsearch node.
 * 
 * @author alexcojocaru
 * 
 * @goal stop
 * @phase post-integration-test
 */
public class StopElasticsearchNodeMojo extends AbstractElasticsearchNodeMojo
{
    @Override
    public void execute() throws MojoExecutionException
    {
        getNode().stop();
    }
}

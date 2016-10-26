package com.github.alexcojocaru.mojo.elasticsearch;

import java.io.IOException;

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
        if (getNode() != null )
        {
            try {
                getNode().stop();
            } catch (IOException e) {
                throw new MojoExecutionException( "StopElasticsearchNodeMojo failed to stop the Elasticsearch node", e );
            }
        }
    }
}

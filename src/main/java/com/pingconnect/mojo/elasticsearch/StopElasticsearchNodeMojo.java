package com.pingconnect.mojo.elasticsearch;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which stops the local Elasticsearch node.
 * 
 * @author alexcojocaru
 * 
 * @goal stop
 * @phase post-integration-test
 */
public class StopElasticsearchNodeMojo extends AbstractMojo
{

    public void execute() throws MojoExecutionException
    {
        ElasticSearchNode.stop();
    }
}

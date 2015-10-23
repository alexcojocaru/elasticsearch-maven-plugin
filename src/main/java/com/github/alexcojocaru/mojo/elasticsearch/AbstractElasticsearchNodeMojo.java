package com.github.alexcojocaru.mojo.elasticsearch;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Abstract class to support starting a local Elasticsearch node.
 * 
 * @author alexcojocaru
 */
public abstract class AbstractElasticsearchNodeMojo extends AbstractMojo
{
    /**
     * @parameter
     * @required
     */
    protected String clusterName;

    /**
     * @parameter
     * @required
     */
    protected Integer httpPort;

    protected ElasticsearchNode getNode() throws MojoExecutionException
    {
        return (ElasticsearchNode) super.getPluginContext().get(clusterName);
    }
 
}

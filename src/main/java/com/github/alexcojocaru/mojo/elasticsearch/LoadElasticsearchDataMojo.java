package com.github.alexcojocaru.mojo.elasticsearch;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author alexcojocaru
 * 
 * @goal load
 * @phase pre-integration-test
 *
 */
public class LoadElasticsearchDataMojo extends AbstractElasticsearchNodeMojo
{
    /**
     * @parameter
     * @required
     */
    private File scriptFile;

    @SuppressWarnings("unchecked")
    @Override
    public void execute() throws MojoExecutionException {
        LoadElasticsearchUtility.load(scriptFile, getLog(),httpPort);
    }
}

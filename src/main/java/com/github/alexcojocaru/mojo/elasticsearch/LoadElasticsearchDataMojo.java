package com.github.alexcojocaru.mojo.elasticsearch;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author alexcojocaru
 * 
 * @goal load
 * @phase pre-integration-test
 *
 */
public class LoadElasticsearchDataMojo extends AbstractMojo
{
    /**
     * @parameter
     * @required
     */
    private File scriptFile;

    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException {
        LoadElasticSearchUtility.load(scriptFile, getLog());
    }
}

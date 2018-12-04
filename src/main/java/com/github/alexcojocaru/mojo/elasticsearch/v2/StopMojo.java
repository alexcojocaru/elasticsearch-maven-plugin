package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.apache.commons.exec.CommandLine;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.github.alexcojocaru.mojo.elasticsearch.v2.util.ProcessUtil;

/**
 * The main plugin mojo to stop a forked ES instances.
 * 
 * @author Alex Cojocaru
 */
@Mojo(name = "stop", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST, threadSafe = true)
public class StopMojo
        extends AbstractElasticsearchBaseMojo
{

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (skip)
        {
            getLog().info("Skipping plugin execution");
            return;
        }

        ClusterConfiguration clusterConfig = buildClusterConfiguration();

        for (InstanceConfiguration config : clusterConfig.getInstanceConfigurationList())
        {
            try
            {
                getLog().info(String.format("Stopping Elasticsearch [%s]", config));

                String baseDir = config.getBaseDir();
                ProcessUtil.executeScript(config, getShutdownScriptCommand(baseDir));

                getLog().info(String.format("Elasticsearch [%d] stopped", config.getId()));
                ProcessUtil.cleanupPid(baseDir);
            }
            catch (Exception e)
            {
                getLog().error("Exception while stopping Elasticsearch", e);
            }
        }
    }

    protected CommandLine getShutdownScriptCommand(String basePath)
    {
        String pid = ProcessUtil.getElasticsearchPid(basePath);
        return ProcessUtil.buildKillCommandLine(pid);
    }

}

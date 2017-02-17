package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang3.SystemUtils;
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
@Mojo(name = "stop", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
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
            }
            catch (Exception e)
            {
                getLog().error("Exception while stopping Elasticsearch", e);
            }
        }
    }

    protected CommandLine getShutdownScriptCommand(String basePath)
    {
        String pid;
        try
        {
            pid = new String(Files.readAllBytes(Paths.get(basePath, " pid")));
        }
        catch (IOException e)
        {
            getLog().error("Cannot read the PID of the Elasticsearch process from the pid file");
            throw new IllegalStateException(e);
        }

        CommandLine command;

        if (SystemUtils.IS_OS_WINDOWS)
        {
            command = new CommandLine("taskkill")
                    .addArgument("/F")
                    .addArgument("/pid")
                    .addArgument(pid);
        }
        else
        {
            command = new CommandLine("kill").addArgument(pid);
        }
        
        return command;

    }

}

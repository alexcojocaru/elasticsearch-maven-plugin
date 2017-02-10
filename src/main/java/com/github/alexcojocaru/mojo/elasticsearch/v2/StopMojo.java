package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

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

                ProcessBuilder processBuilder =
                        new ProcessBuilder(getShutdownScriptCommand(baseDir));
                processBuilder.directory(new File(baseDir));
                processBuilder.redirectErrorStream(true);

                Process process = processBuilder.start();
                int exitValue = process.waitFor();

                getLog().info(String.format(
                        "Elasticsearch [%d] stopped with exit code %d",
                        config.getId(),
                        exitValue));
            }
            catch (Exception e)
            {
                getLog().error("Exception while stopping Elasticsearch", e);
            }
        }
    }

    protected String[] getShutdownScriptCommand(String basePath)
    {
        List<String> cmd = new ArrayList<>();

        String pid;
        try
        {
            pid = new String(Files.readAllBytes(Paths.get(basePath, " pid")));
        }
        catch (IOException e)
        {
            getLog().error("Cannot read the PID of the Elasticsearch process from the pid file");
            throw new RuntimeException(e);
        }

        if (SystemUtils.IS_OS_WINDOWS)
        {
            cmd.add("taskkill");
            cmd.add("/pid");
            cmd.add("/F");
            cmd.add(pid);
            cmd.add("/F");
        }
        else
        {
            cmd.add("kill");
            cmd.add(pid);
        }

        return cmd.toArray(new String[cmd.size()]);

    }

}

package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;

import com.github.alexcojocaru.mojo.elasticsearch.v2.step.ForkedSetupSequence;
import com.github.alexcojocaru.mojo.elasticsearch.v2.step.StepSequence;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.ElasticsearchUtil;

/**
 * Start an ES instance and hold the reference to the ES {@link Process}.
 * 
 * @author Alex Cojocaru
 */
public class ForkedInstance
        implements Runnable
{

    private final InstanceContext context;
    private Process process;

    /**
     * @param context
     */
    public ForkedInstance(InstanceContext context)
    {
        this.context = context;
    }

    /**
     * Set up the instance.
     * 
     * @param mavenPluginContext
     */
    public void configureInstance()
    {
        getSetupSequence().execute(context);
    }

    @Override
    public void run()
    {
        setStartScriptPermissions();
        startElasticsearch();
    }

    private void startElasticsearch()
    {
        File baseDir = new File(context.getConfiguration().getBaseDir());

        ProcessBuilder processBuilder = new ProcessBuilder(getStartScriptCommand());
        processBuilder.directory(baseDir);
        processBuilder.redirectErrorStream(true);

        try
        {
            context.getLog().info(String.format(
                    "Starting Elasticsearch in directory '%s' with arguments '%s'",
                    processBuilder.directory(),
                    String.join(" ", processBuilder.command())));

            process = processBuilder.start();

            Runtime.getRuntime().addShutdownHook(
                    new ForkedElasticsearchProcessShutdownHook(process, context.getLog()));

            int exitValue = process.waitFor();
            context.getLog().info(String.format(
                    "Elasticsearch [%d] stopped with exit code %d",
                    context.getConfiguration().getId(),
                    exitValue));
        }
        catch (InterruptedException | IOException e)
        {
            context.getLog().error("Cannot start Elasticsearch", e);
        }
    }

    private void setStartScriptPermissions()
    {
        if (SystemUtils.IS_OS_WINDOWS)
        {
            // do we have filepermissions on windows
            return;
        }

        File baseDir = new File(context.getConfiguration().getBaseDir());
        File binDirectory = ElasticsearchUtil.getBinDirectory(baseDir);

        ProcessBuilder processBuilder = new ProcessBuilder("chmod", "755", "elasticsearch");
        processBuilder.directory(binDirectory);
        processBuilder.redirectErrorStream(true);

        try
        {
            Process p = processBuilder.start();
            int exitValue = p.waitFor();
            context.getLog().debug(String.format(
                    "SetStartScriptPermission finished with exit code %d",
                    exitValue));
        }
        catch (InterruptedException | IOException e)
        {
            context.getLog().error(
                    "Cannot set the 755 permissions on the start scirpt 'bin/elasticsearch'");

            throw new ElasticsearchSetupException(e.getMessage(), e);
        }
    }

    private StepSequence getSetupSequence()
    {
        StepSequence sequence = new ForkedSetupSequence();
        return sequence;
    }

    protected String[] getStartScriptCommand()
    {
        List<String> cmd = new ArrayList<>();

        if (SystemUtils.IS_OS_WINDOWS)
        {
            cmd.add("cmd");
            cmd.add("/c");
            cmd.add(".\\bin\\elasticsearch");
        }
        else
        {
            cmd.add("./bin/elasticsearch");
        }

        // Write the PID to a file, to be used to shut down the instance
        cmd.add("-p pid");

        cmd.add("-Ecluster.name=" + context.getConfiguration().getClusterName());
        cmd.add("-Ehttp.port=" + context.getConfiguration().getHttpPort());
        cmd.add("-Etransport.tcp.port=" + context.getConfiguration().getTransportPort());

        if (context.getConfiguration().isAutoCreateIndex() == false)
        {
            cmd.add("-Eaction.auto_create_index=false");
        }

        cmd.add("-Ehttp.cors.enabled=true");

        return cmd.toArray(new String[cmd.size()]);
    }

}

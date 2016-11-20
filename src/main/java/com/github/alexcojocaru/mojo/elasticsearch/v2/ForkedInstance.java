package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.logging.Log;

import com.github.alexcojocaru.mojo.elasticsearch.v2.step.InstanceSetupSequence;
import com.github.alexcojocaru.mojo.elasticsearch.v2.step.InstanceStepSequence;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.ElasticsearchUtil;

/**
 * Start an ES instance and hold the reference to the ES {@link Process}.
 * 
 * @author Alex Cojocaru
 */
public class ForkedInstance
        implements Runnable
{

    private final InstanceConfiguration config;
    private Process process;

    public ForkedInstance(InstanceConfiguration config)
    {
        this.config = config;
    }

    public void configureInstance()
    {
        getSetupSequence().execute(config);
    }

    @Override
    public void run()
    {
        setStartScriptPermissions();
        startElasticsearch();
    }

    private void startElasticsearch()
    {
        File baseDir = new File(config.getBaseDir());

        ProcessBuilder processBuilder = new ProcessBuilder(getStartScriptCommand());
        processBuilder.directory(baseDir);
        processBuilder.redirectErrorStream(true);
        
        Log log = config.getClusterConfiguration().getLog();

        try
        {
            log.info(String.format(
                    "Starting Elasticsearch [%d] in directory '%s' with arguments '%s'",
                    config.getId(),
                    processBuilder.directory(),
                    String.join(" ", processBuilder.command())));

            process = processBuilder.start();

            Runtime.getRuntime().addShutdownHook(
                    new ForkedElasticsearchProcessShutdownHook(process, log));

            int exitValue = process.waitFor();
            log.info(String.format(
                    "Elasticsearch [%d] stopped with exit code %d",
                    config.getId(),
                    exitValue));
        }
        catch (InterruptedException | IOException e)
        {
            log.error("Cannot start Elasticsearch", e);
        }
    }

    private void setStartScriptPermissions()
    {
        if (SystemUtils.IS_OS_WINDOWS)
        {
            // do we have filepermissions on windows
            return;
        }

        File baseDir = new File(config.getBaseDir());
        File binDirectory = ElasticsearchUtil.getBinDirectory(baseDir);

        ProcessBuilder processBuilder = new ProcessBuilder("chmod", "755", "elasticsearch");
        processBuilder.directory(binDirectory);
        processBuilder.redirectErrorStream(true);

        Log log = config.getClusterConfiguration().getLog();

        try
        {
            Process p = processBuilder.start();
            int exitValue = p.waitFor();
            log.debug(String.format(
                    "SetStartScriptPermission finished with exit code %d",
                    exitValue));
        }
        catch (InterruptedException | IOException e)
        {
            log.error("Cannot set the 755 permissions on the start scirpt 'bin/elasticsearch'");

            throw new ElasticsearchSetupException(e.getMessage(), e);
        }
    }

    private InstanceStepSequence getSetupSequence()
    {
        InstanceStepSequence sequence = new InstanceSetupSequence();
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

        cmd.add("-Ecluster.name=" + config.getClusterConfiguration().getClusterName());
        cmd.add("-Ehttp.port=" + config.getHttpPort());
        cmd.add("-Etransport.tcp.port=" + config.getTransportPort());

        if (config.getClusterConfiguration().isAutoCreateIndex() == false)
        {
            cmd.add("-Eaction.auto_create_index=false");
        }
        
        String pathScripts = config.getClusterConfiguration().getPathScripts();
        if (StringUtils.isNotBlank(pathScripts))
        {
            cmd.add("-Epath.scripts=" + pathScripts);
        }

        cmd.add("-Ehttp.cors.enabled=true");

        return cmd.toArray(new String[cmd.size()]);
    }

}

package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.github.alexcojocaru.mojo.elasticsearch.v2.step.InstanceSetupSequence;
import com.github.alexcojocaru.mojo.elasticsearch.v2.step.InstanceStepSequence;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.FilesystemUtil;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.ProcessUtil;

/**
 * Start an ES instance and hold the reference to the ES {@link Process}.
 * 
 * @author Alex Cojocaru
 */
public class ForkedInstance
        implements Runnable
{

    private final InstanceConfiguration config;

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
        FilesystemUtil.setScriptPermission(config, "elasticsearch");
        startElasticsearch();
    }

    private void startElasticsearch()
    {
        ProcessUtil.executeScript(config,
                getStartScriptCommand(),
                new ForkedElasticsearchProcessShutdownHook(config));
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
            File scriptsDir = new File(pathScripts);
            cmd.add("-Epath.scripts=" + scriptsDir.getAbsolutePath());
        }

        cmd.add("-Ehttp.cors.enabled=true");

        return cmd.toArray(new String[cmd.size()]);
    }

}

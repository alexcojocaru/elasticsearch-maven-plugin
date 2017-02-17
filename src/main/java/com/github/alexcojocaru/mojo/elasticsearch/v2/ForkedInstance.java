package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.File;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang3.StringUtils;

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

        ProcessUtil.executeScript(config,
                getStartScriptCommand(),
                null,
                new ForkedElasticsearchProcessDestroyer(config));
    }

    private InstanceStepSequence getSetupSequence()
    {
        InstanceStepSequence sequence = new InstanceSetupSequence();
        return sequence;
    }

    protected CommandLine getStartScriptCommand()
    {
        CommandLine cmd = ProcessUtil.buildCommandLine("bin/elasticsearch");

        // Write the PID to a file, to be used to shut down the instance
        cmd.addArgument("-p pid", false);

        cmd.addArgument("-Ecluster.name=" + config.getClusterConfiguration().getClusterName(), false);
        cmd.addArgument("-Ehttp.port=" + config.getHttpPort(), false);
        cmd.addArgument("-Etransport.tcp.port=" + config.getTransportPort(), false);

        if (config.getClusterConfiguration().isAutoCreateIndex() == false)
        {
            cmd.addArgument("-Eaction.auto_create_index=false", false);
        }
        
        String pathScripts = config.getClusterConfiguration().getPathScripts();
        if (StringUtils.isNotBlank(pathScripts))
        {
            File scriptsDir = new File(pathScripts);
            cmd.addArgument("-Epath.scripts=" + scriptsDir.getAbsolutePath(), false);
        }

        cmd.addArgument("-Ehttp.cors.enabled=true");

        return cmd;
    }

}

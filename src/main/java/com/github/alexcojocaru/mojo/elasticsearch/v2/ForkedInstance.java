package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.util.List;
import java.util.stream.Collectors;

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

        final ForkedElasticsearchProcessDestroyer processDestroyer = new ForkedElasticsearchProcessDestroyer(config);
        Runtime.getRuntime().addShutdownHook(new Thread(processDestroyer));

        ProcessUtil.executeScript(config,
                getStartScriptCommand(),
                config.getEnvironmentVariables(),
                processDestroyer);
    }

    private InstanceStepSequence getSetupSequence()
    {
        InstanceStepSequence sequence = new InstanceSetupSequence();
        return sequence;
    }

    protected CommandLine getStartScriptCommand()
    {
        CommandLine cmd = ProcessUtil.buildCommandLine("bin/elasticsearch");

        // Write the PID to a file, to be used to shut down the instance.
        // The option ("-p") and the pid file name ("pid") must be provides as separate argument
        // otherwise, if they are provided as one ("-p pid"), with the way the Java command line
        // arguments are parsed, the actual file name will be ' pid' (with a leading space),
        // which creates issues on Windows.
        cmd.addArgument("-p", false);
        cmd.addArgument("pid", false);

        cmd.addArgument(
        		"-Ecluster.name=" + config.getClusterConfiguration().getClusterName(),
        		false);
        cmd.addArgument("-Ehttp.port=" + config.getHttpPort(), false);
        cmd.addArgument("-Etransport.tcp.port=" + config.getTransportPort(), false);

        // If there are multiple nodes, I need to tell each about the other,
        // in order to form a cluster.
        List<String> hosts = config.getClusterConfiguration().getInstanceConfigurationList()
        		.stream()
        		.filter(config -> config != this.config)
        		.map(config -> "127.0.0.1:" + config.getTransportPort())
        		.collect(Collectors.toList());
		if (hosts.isEmpty() == false)
		{
			String hostsString = StringUtils.join(hosts, ',');
			cmd.addArgument("-Ediscovery.zen.ping.unicast.hosts=" + hostsString, false);
		}

        if (config.getClusterConfiguration().isAutoCreateIndex() == false)
        {
            cmd.addArgument("-Eaction.auto_create_index=false", false);
        }

        cmd.addArgument("-Ehttp.cors.enabled=true");

        if (config.getSettings() != null)
        {
            config.getSettings().forEach((key, value) -> cmd.addArgument("-E" + key + '=' + value));
        }

        return cmd;
    }

}

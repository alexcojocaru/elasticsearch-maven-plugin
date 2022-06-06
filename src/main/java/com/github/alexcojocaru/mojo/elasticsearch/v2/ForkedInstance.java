package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.util.stream.Collectors;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang3.StringUtils;

import com.github.alexcojocaru.mojo.elasticsearch.v2.step.InstanceSetupSequence;
import com.github.alexcojocaru.mojo.elasticsearch.v2.step.InstanceStepSequence;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.FilesystemUtil;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.ProcessUtil;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.VersionUtil;

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
        
        // Any settings that can be specified in the config file can also be specified
        // on the command line, using the -E syntax
        // https://www.elastic.co/guide/en/elasticsearch/reference/current/targz.html#targz-configuring

        cmd.addArgument(
                "-Ecluster.name=" + config.getClusterConfiguration().getClusterName(),
                false);

        cmd.addArgument("-Ehttp.port=" + config.getHttpPort(), false);

        String transportPortName = VersionUtil
                .isEqualOrGreater_8_0_0(config.getClusterConfiguration().getVersion())
                        ? "transport.port"
                        : "transport.tcp.port";
        cmd.addArgument("-E" + transportPortName + "=" + config.getTransportPort(), false);
        
        // https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-discovery-settings.html
        if (VersionUtil.isEqualOrGreater_7_0_0(config.getClusterConfiguration().getVersion()))
        {
            if (config.getClusterConfiguration().getInstanceConfigurationList().size() > 1) {
                // default discovery.type is 'multi-node'
                // I need to tell each node about the other, in order to form a cluster.
                String hosts = config.getClusterConfiguration().getInstanceConfigurationList()
                        .stream()
                        .map(config -> "127.0.0.1:" + config.getTransportPort())
                        .collect(Collectors.joining(","));
                // https://www.elastic.co/guide/en/elasticsearch/reference/current/important-settings.html#initial_master_nodes
                cmd.addArgument("-Ediscovery.seed_hosts=" + hosts, false);
                cmd.addArgument("-Ecluster.initial_master_nodes=" + hosts, false);
            } else {
                cmd.addArgument("-Ediscovery.type=single-node", false);
            }
        }
        else
        {
            String hosts = config.getClusterConfiguration().getInstanceConfigurationList()
                    .stream()
                    .filter(instanceConfig -> this.config != instanceConfig)
                    .map(config -> "127.0.0.1:" + config.getTransportPort())
                    .collect(Collectors.joining(","));
            if (StringUtils.isNotEmpty(hosts)) {
                // https://www.elastic.co/guide/en/elasticsearch/reference/6.8/discovery-settings.html
                cmd.addArgument("-Ediscovery.zen.ping.unicast.hosts=" + hosts, false);
            }
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

        if (VersionUtil.isEqualOrGreater_8_0_0(config.getClusterConfiguration().getVersion()))
        {
            cmd.addArgument("-Expack.security.enabled=false", false);
        }

        return cmd;
    }

}

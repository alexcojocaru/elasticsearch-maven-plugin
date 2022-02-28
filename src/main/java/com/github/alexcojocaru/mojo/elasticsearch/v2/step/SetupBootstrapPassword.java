package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import org.apache.commons.exec.CommandLine;
import org.apache.maven.plugin.logging.Log;

import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.FilesystemUtil;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.ProcessUtil;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.VersionUtil;

/**
 * Set up the keystore and add the bootstrap password.
 *
 * @author Alex Cojocaru
 */
public class SetupBootstrapPassword
        implements InstanceStep
{
    @Override
    public void execute(InstanceConfiguration config)
    {
        Log log = config.getClusterConfiguration().getLog();

        log.info(String.format("Elasticsearch version is %s", config.getClusterConfiguration().getVersion()));

        if (VersionUtil.isEqualOrGreater_8_0_0(config.getClusterConfiguration().getVersion()))
        {
            log.info("Need to set up the keystore and add the bootstrap password");

            FilesystemUtil.setScriptPermission(config, "elasticsearch-keystore");

            // https://www.elastic.co/guide/en/elasticsearch/reference/current/elasticsearch-keystore.html#add-string-to-keystore
            // https://www.elastic.co/guide/en/elasticsearch/reference/current/built-in-users.html#bootstrap-elastic-passwords

            log.info("Creating a keystore with no password");
            CommandLine cmdCreateKeystore = ProcessUtil.buildCommandLine("bin/elasticsearch-keystore")
                    .addArgument("create");
            ProcessUtil.executeScript(config, cmdCreateKeystore);

            log.info("Setting the bootstrap password to 'password'");
            CommandLine cmdSetPassword = ProcessUtil.buildCommandLine("bin/elasticsearch-keystore")
                    .addArgument("add")
                    // will be passing the password as command input, this tells the keystore cmd to read it
                    .addArgument("--stdin")
                    .addArgument("bootstrap.password");
            ProcessUtil.executeScript(
                    config,
                    cmdSetPassword,
                    config.getClusterConfiguration().getBootstrapPassword());
        }
        else
        {
            log.info("No need to set up the keystore or add the bootstrap password");
        }
    }
}

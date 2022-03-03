package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.alexcojocaru.mojo.elasticsearch.v2.util.VersionUtil;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.FilesystemUtil;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.ProcessUtil;

/**
 * Remove the plugins directory from the current Elasticsearch instance, if any.
 * That prevents the plugin installation from failing when the ES directory is not cleaned up.
 * (https://github.com/alexcojocaru/elasticsearch-maven-plugin/issues/41).
 *
 * @author Alex Cojocaru
 */
public class RemovePluginsStep
        implements InstanceStep
{
    @Override
    public void execute(InstanceConfiguration config)
    {
        Log log = config.getClusterConfiguration().getLog();
        
        File pluginsDir = new File(config.getBaseDir(), "plugins");
        try
        {
            log.debug(String.format(
                    "Checking if the plugins directory with path: '%s' exists",
                    pluginsDir.getCanonicalPath()));
        }
        catch (IOException e)
        {
            throw new ElasticsearchSetupException(
                    "Cannot check if the plugins directory exists",
                    e);
        }

        if (pluginsDir.exists())
        {
            log.debug("The plugins directory exists; removing all installed plugins");

            if (VersionUtil.isEqualOrGreater_6_4_0(config.getClusterConfiguration().getVersion()))
            {
                FilesystemUtil.setScriptPermission(config, "elasticsearch-cli");
            }
            FilesystemUtil.setScriptPermission(config, "elasticsearch-plugin");

            CommandLine cmd = ProcessUtil.buildCommandLine("bin/elasticsearch-plugin")
                    .addArgument("list");
            
            List<String> output = ProcessUtil.executeScript(config, cmd, config.getEnvironmentVariables());
            // remove empty entries and trim
            List<String> pluginNames = output.stream()
                    .map(String::trim)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toCollection(ArrayList::new));

            for (String pluginName : pluginNames)
            {
                log.info(String.format("Removing plugin '%s'", pluginName));
                
                CommandLine removeCmd = ProcessUtil.buildCommandLine("bin/elasticsearch-plugin")
                        .addArgument("remove")
                        .addArgument(pluginName);
                
                ProcessUtil.executeScript(config, removeCmd, config.getEnvironmentVariables());
            }
        }
        else
        {
            log.debug("The plugins directory does not exist");
        }
    }
}

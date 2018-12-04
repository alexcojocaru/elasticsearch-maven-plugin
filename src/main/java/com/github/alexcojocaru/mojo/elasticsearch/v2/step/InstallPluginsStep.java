package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import java.util.HashMap;
import java.util.Map;

import com.github.alexcojocaru.mojo.elasticsearch.v2.util.VersionUtil;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;

import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.PluginConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.FilesystemUtil;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.ProcessUtil;

/**
 * Install the required plugins into the current Elasticsearch instance.
 *
 * @author Alex Cojocaru
 */
public class InstallPluginsStep
        implements InstanceStep
{
    @Override
    public void execute(InstanceConfiguration config)
    {
        if (config.getClusterConfiguration().getPlugins().size() > 0)
        {
            if (VersionUtil.isEqualOrGreater_6_4_0(config.getClusterConfiguration().getVersion()))
            {
                FilesystemUtil.setScriptPermission(config, "elasticsearch-cli");
            }
            FilesystemUtil.setScriptPermission(config, "elasticsearch-plugin");
        }

        Log log = config.getClusterConfiguration().getLog();
        
        for (PluginConfiguration plugin : config.getClusterConfiguration().getPlugins())
        {
            log.info(String.format(
                    "Installing plugin '%s' with options '%s'",
                    plugin.getUri(), plugin.getEsJavaOpts()));
            
            Map<String, String> environment = null;
            
            if (StringUtils.isNotBlank(plugin.getEsJavaOpts()))
            {
                environment = new HashMap<>();
                environment.put("ES_JAVA_OPTS", plugin.getEsJavaOpts());
            }

            CommandLine cmd = ProcessUtil.buildCommandLine("bin/elasticsearch-plugin")
                    .addArgument("install")
                    .addArgument("--batch")
                    .addArgument(plugin.getUri());
            
            ProcessUtil.executeScript(config, cmd, ProcessUtil.createEnvironment(environment), null);
        }
    }
}

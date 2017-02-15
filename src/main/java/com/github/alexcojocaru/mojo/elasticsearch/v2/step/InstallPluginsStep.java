package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
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
            FilesystemUtil.setScriptPermission(config, "elasticsearch-plugin");
        }

        Log log = config.getClusterConfiguration().getLog();
        
        for (PluginConfiguration plugin : config.getClusterConfiguration().getPlugins())
        {
            log.debug(String.format(
                    "Found plugin '%s' with options '%s'",
                    plugin.getUri(), plugin.getEsJavaOpts()));
            
            List<String> cmd = new ArrayList<>();
            
            if (SystemUtils.IS_OS_WINDOWS)
            {
                cmd.add("cmd");
                cmd.add("/c");
                cmd.add(".\\bin\\elasticsearch-plugin");
            }
            else
            {
                // How do I do this on Windows ?
                if (StringUtils.isNotBlank(plugin.getEsJavaOpts()))
                {
                    cmd.add(String.format("ES_JAVA_OPTS=\"%s\"", plugin.getEsJavaOpts()));
                }
                
                cmd.add("./bin/elasticsearch-plugin");
            }

            cmd.add("install");
            cmd.add("--batch");
            cmd.add(plugin.getUri());
            
            String[] cmdArray = cmd.toArray(new String[cmd.size()]);
            ProcessUtil.executeScript(config, cmdArray, null);
        }
    }
}

package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.maven.plugin.logging.Log;

import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.PluginArtifactResolver;

/**
 * The cluster configuration, containing the list of ES configurations,
 * the artifact resolver, the logger, and other cluster specific attributes.
 * 
 * @author Alex Cojocaru
 */
public class ClusterConfiguration
{
    private List<InstanceConfiguration> instanceConfigurationList;
    private PluginArtifactResolver artifactResolver;
    private Log log;

    private String version;
    private String clusterName;
    private String pathScripts;
    private String pathInitScript;
    private boolean keepExistingData;
    private int timeout;
    private boolean setAwait;
    private boolean autoCreateIndex;

    public ClusterConfiguration(List<InstanceConfiguration> instanceConfigurationList,
            PluginArtifactResolver artifactResolver,
            Log log)
    {
        this.instanceConfigurationList = instanceConfigurationList;
        this.artifactResolver = artifactResolver;
        this.log = log;
    }

    public List<InstanceConfiguration> getInstanceConfigurationList()
    {
        return instanceConfigurationList;
    }

    public PluginArtifactResolver getArtifactResolver()
    {
        return artifactResolver;
    }

    public Log getLog()
    {
        return log;
    }

    public String getVersion()
    {
        return version;
    }

    public String getClusterName()
    {
        return clusterName;
    }
    
    public String getPathScripts()
    {
        return pathScripts;
    }

    public String getPathInitScript()
    {
        return pathInitScript;
    }

    public boolean isKeepExistingData()
    {
        return keepExistingData;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public boolean isSetAwait()
    {
        return setAwait;
    }

    public boolean isAutoCreateIndex()
    {
        return autoCreateIndex;
    }

    public String toString()
    {
        return new ToStringBuilder(this)
                .append("version", version)
                .append("clusterName", clusterName)
                .append("pathScripts", pathScripts)
                .append("pathInitScript", pathInitScript)
                .append("keepExistingData", keepExistingData)
                .append("timeout", timeout)
                .append("setAwait", setAwait)
                .append("autoCreateIndex", autoCreateIndex)
                .append("instanceConfigurationList", StringUtils.join(instanceConfigurationList, ','))
                .toString();
    }

    public static class Builder
    {
        private List<InstanceConfiguration> instanceConfigurationList = new ArrayList<>();
        private  PluginArtifactResolver artifactResolver;
        private  Log log;

        private String version;
        private String clusterName;
        private String pathScripts;
        private String pathInitScript;
        private boolean keepExistingData;
        private int timeout;
        private boolean setAwait;
        private boolean autoCreateIndex;
        
        
        public Builder addInstanceConfiguration(InstanceConfiguration config)
        {
            this.instanceConfigurationList.add(config);
            return this;
        }
        
        public Builder withArtifactResolver(PluginArtifactResolver artifactResolver)
        {
            this.artifactResolver = artifactResolver;
            return this;
        }

        public Builder withLog(Log log)
        {
            this.log = log;
            return this;
        }

        public Builder withVersion(String version)
        {
            this.version = version;
            return this;
        }

        public Builder withClusterName(String clusterName)
        {
            this.clusterName = clusterName;
            return this;
        }

        public Builder withPathScripts(String pathScripts)
        {
            this.pathScripts = pathScripts;
            return this;
        }

        public Builder withPathInitScript(String pathInitScript)
        {
            this.pathInitScript = pathInitScript;
            return this;
        }

        public Builder withKeepExistingData(boolean keepExistingData)
        {
            this.keepExistingData = keepExistingData;
            return this;
        }

        public Builder withTimeout(int timeout)
        {
            this.timeout = timeout;
            return this;
        }

        public Builder withSetAwait(boolean setAwait)
        {
            this.setAwait = setAwait;
            return this;
        }

        public Builder withAutoCreateIndex(boolean autoCreateIndex)
        {
            this.autoCreateIndex = autoCreateIndex;
            return this;
        }

        public ClusterConfiguration build()
        {
            ClusterConfiguration config = new ClusterConfiguration(
                    instanceConfigurationList, artifactResolver, log);

            config.version = version;
            config.clusterName = clusterName;
            config.pathScripts = pathScripts;
            config.pathInitScript = pathInitScript;
            config.keepExistingData = keepExistingData;
            config.timeout = timeout;
            config.setAwait = setAwait;
            config.autoCreateIndex = autoCreateIndex;
            
            config.getInstanceConfigurationList().forEach(c -> c.setClusterConfiguration(config));

            return config;
        }
    }

}

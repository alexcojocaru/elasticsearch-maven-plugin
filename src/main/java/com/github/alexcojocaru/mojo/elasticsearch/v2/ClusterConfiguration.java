package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.maven.plugin.logging.Log;

import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.PluginArtifactInstaller;
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
    private PluginArtifactInstaller artifactInstaller;
    private Log log;

    private String flavour;
    private String version;
    private String downloadUrl;
    private String downloadUrlUsername;
    private String downloadUrlPassword;
    private String clusterName;
    private String pathConf;
    private List<PluginConfiguration> plugins;
    private List<String> pathInitScripts;
    private boolean keepExistingData;
    private int startupTimeout;
    private int clientSocketTimeout;
    private boolean setAwait;
    private boolean autoCreateIndex;

    private ClusterConfiguration(List<InstanceConfiguration> instanceConfigurationList,
            PluginArtifactResolver artifactResolver,
            PluginArtifactInstaller artifactInstaller,
            Log log)
    {
        this.instanceConfigurationList = instanceConfigurationList;
        this.artifactResolver = artifactResolver;
        this.artifactInstaller = artifactInstaller;
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

    public PluginArtifactInstaller getArtifactInstaller() {
        return artifactInstaller;
    }

    public Log getLog()
    {
        return log;
    }

    public String getFlavour() {
        return flavour;
    }

    public String getVersion()
    {
        return version;
    }

    public String getDownloadUrl()
    {
        return downloadUrl;
    }

    public String getDownloadUrlUsername()
    {
        return downloadUrlUsername;
    }

    public String getDownloadUrlPassword()
    {
        return downloadUrlPassword;
    }

    public String getClusterName()
    {
        return clusterName;
    }

    public String getPathConf()
    {
        return pathConf;
    }

    public List<PluginConfiguration> getPlugins()
    {
        return plugins;
    }

    public List<String> getPathInitScripts()
    {
        return pathInitScripts;
    }

    public boolean isKeepExistingData()
    {
        return keepExistingData;
    }

    public int getStartupTimeout()
    {
        return startupTimeout;
    }

    public int getClientSocketTimeout() {
        return clientSocketTimeout;
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
                .append("flavour", flavour)
                .append("version", version)
                .append("downloadUrl", downloadUrl)
                .append("downloadUrlUsername", downloadUrlUsername)
                .append("downloadUrlPassword", Optional.ofNullable(downloadUrlPassword).map(p -> "****").orElse(null))
                .append("clusterName", clusterName)
                .append("pathConfigFile", pathConf)
                .append("plugins", plugins)
                .append("pathInitScripts", pathInitScripts)
                .append("keepExistingData", keepExistingData)
                .append("startupTimeout", startupTimeout)
                .append("clientSocketTimeout", clientSocketTimeout)
                .append("setAwait", setAwait)
                .append("autoCreateIndex", autoCreateIndex)
                .append("instanceConfigurationList", StringUtils.join(instanceConfigurationList, ','))
                .toString();
    }

    public static class Builder
    {
        private List<InstanceConfiguration> instanceConfigurationList = new ArrayList<>();
        private PluginArtifactResolver artifactResolver;
        private PluginArtifactInstaller artifactInstaller;
        private Log log;

        private String flavour;
        private String version;
        private String downloadUrl;
        private String downloadUrlUsername;
        private String downloadUrlPassword;
        private String clusterName;
        private String pathConf;
        private List<PluginConfiguration> plugins;
        private List<String> pathInitScripts;
        private boolean keepExistingData;
        private int startupTimeout;
        private int clientSocketTimeout;
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

        public Builder withArtifactInstaller(PluginArtifactInstaller artifactInstaller)
        {
            this.artifactInstaller = artifactInstaller;
            return this;
        }

        public Builder withLog(Log log)
        {
            this.log = log;
            return this;
        }

        public Builder withFlavour(String flavour)
        {
            this.flavour = flavour;
            return this;
        }

        public Builder withVersion(String version)
        {
            this.version = version;
            return this;
        }

        public Builder withDownloadUrl(String downloadUrl)
        {
            this.downloadUrl = downloadUrl;
            return this;
        }

        public Builder withDownloadUrlUsername(String downloadUrlUsername)
        {
            this.downloadUrlUsername = downloadUrlUsername;
            return this;
        }

        public Builder withDownloadUrlPassword(String downloadUrlPassword)
        {
            this.downloadUrlPassword = downloadUrlPassword;
            return this;
        }

        public Builder withClusterName(String clusterName)
        {
            this.clusterName = clusterName;
            return this;
        }

        public Builder withPathConf(String pathConf)
        {
            this.pathConf = pathConf;
            return this;
        }

        public Builder withElasticsearchPlugins(List<PluginConfiguration> plugins)
        {
            this.plugins = plugins;
            return this;
        }

        public Builder withPathInitScripts(List<String> pathInitScripts)
        {
            this.pathInitScripts = pathInitScripts;
            return this;
        }

        public Builder withKeepExistingData(boolean keepExistingData)
        {
            this.keepExistingData = keepExistingData;
            return this;
        }

        public Builder withStartupTimeout(int startupTimeout)
        {
            this.startupTimeout = startupTimeout;
            return this;
        }

        public Builder withClientSocketTimeout(int clientSocketTimeout)
        {
            this.clientSocketTimeout = clientSocketTimeout;
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
                    instanceConfigurationList, artifactResolver, artifactInstaller, log);

            config.flavour = flavour;
            config.version = version;
            config.downloadUrl = downloadUrl;
            config.downloadUrlUsername = downloadUrlUsername;
            config.downloadUrlPassword = downloadUrlPassword;
            config.clusterName = clusterName;
            config.pathConf = pathConf;
            config.plugins = plugins;
            config.pathInitScripts = pathInitScripts;
            config.keepExistingData = keepExistingData;
            config.startupTimeout = startupTimeout;
            config.clientSocketTimeout = clientSocketTimeout;
            config.setAwait = setAwait;
            config.autoCreateIndex = autoCreateIndex;

            config.getInstanceConfigurationList().forEach(c -> c.setClusterConfiguration(config));

            return config;
        }
    }

}

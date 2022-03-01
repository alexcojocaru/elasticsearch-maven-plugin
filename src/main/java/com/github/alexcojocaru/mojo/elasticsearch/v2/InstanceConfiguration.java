package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Hold configuration of a single Elasticsearch instance,
 * with a reference to the cluster configuration.
 * 
 * @author Alex Cojocaru
 */
public class InstanceConfiguration
{
    private ClusterConfiguration clusterConfiguration;
    private int id;
    private String baseDir;
    private int httpPort;
    private int transportPort;
    private String pathData;
    private String pathLogs;
    private Map<String, String> environmentVariables;
    private Properties settings;
    private int startupTimeout;


    public ClusterConfiguration getClusterConfiguration()
    {
        return clusterConfiguration;
    }

    public void setClusterConfiguration(ClusterConfiguration clusterConfiguration)
    {
        this.clusterConfiguration = clusterConfiguration;
    }

    public int getId()
    {
        return id;
    }

    public String getBaseDir()
    {
        return baseDir;
    }

    public int getHttpPort()
    {
        return httpPort;
    }

    public int getTransportPort()
    {
        return transportPort;
    }

    public String getPathData()
    {
        return pathData;
    }

    public String getPathLogs()
    {
        return pathLogs;
    }

    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public Properties getSettings()
    {
        return settings;
    }

    public int getStartupTimeout()
    {
        return startupTimeout;
    }

    public String toString()
    {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("baseDir", baseDir)
                .append("httpPort", httpPort)
                .append("transportPort", transportPort)
                .append("pathData", pathData)
                .append("pathLogs", pathLogs)
                .append("settings", settings)
                .append("startupTimeout", startupTimeout)
                .toString();
    }

    public static class Builder
    {
        private ClusterConfiguration clusterConfiguration;
        private int id;
        private String baseDir;
        private int httpPort;
        private int transportPort;
        private String pathData;
        private String pathLogs;
        private Map<String, String> environmentVariables;
        private Properties settings;
        private int startupTimeout;
        

        public Builder withClusterConfiguration(ClusterConfiguration clusterConfiguration)
        {
            this.clusterConfiguration = clusterConfiguration;
            return this;
        }

        public Builder withId(int id)
        {
            this.id = id;
            return this;
        }

        public Builder withBaseDir(String baseDir)
        {
            this.baseDir = baseDir;
            return this;
        }

        public Builder withHttpPort(int httpPort)
        {
            this.httpPort = httpPort;
            return this;
        }

        public Builder withTransportPort(int transportPort)
        {
            this.transportPort = transportPort;
            return this;
        }

        public Builder withPathData(String pathData)
        {
            this.pathData = pathData;
            return this;
        }

        public Builder withPathLogs(String pathLogs)
        {
            this.pathLogs = pathLogs;
            return this;
        }

        public Builder withEnvironmentVariables(final Map<String, String> environmentVariables) {
            this.environmentVariables = environmentVariables;
            return this;
        }

        public Builder withSettings(final Properties settings) {
            this.settings = settings;
            return this;
        }

        public Builder withStartupTimeout(int startupTimeout)
        {
            this.startupTimeout = startupTimeout;
            return this;
        }

        public InstanceConfiguration build()
        {
            InstanceConfiguration config = new InstanceConfiguration();

            config.clusterConfiguration = clusterConfiguration;
            config.id = id;
            config.baseDir = baseDir;
            config.httpPort = httpPort;
            config.transportPort = transportPort;
            config.pathData = pathData;
            config.pathLogs = pathLogs;
            config.environmentVariables = environmentVariables == null ? Collections.emptyMap() : environmentVariables;
            config.settings = settings;
            config.startupTimeout = startupTimeout;

            return config;
        }
    }

}

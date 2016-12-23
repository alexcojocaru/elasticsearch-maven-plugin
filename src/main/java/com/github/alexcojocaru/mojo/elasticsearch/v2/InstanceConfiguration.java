package com.github.alexcojocaru.mojo.elasticsearch.v2;

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

    public String toString()
    {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("baseDir", baseDir)
                .append("httpPort", httpPort)
                .append("transportPort", transportPort)
                .append("pathData", pathData)
                .append("pathLogs", pathLogs)
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

            return config;
        }
    }

}

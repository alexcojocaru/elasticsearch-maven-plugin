package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Hold configuration of a single instance.
 * 
 * @author Alex Cojocaru
 */
public class InstanceConfiguration
{
    private int id;
    private String baseDir;
    private int httpPort;
    private int transportPort;
    private String version;
    private String clusterName;
    private String pathData;
    private String pathLogs;
    private String pathInitScript;
    private boolean keepExistingData;
    private int timeout;
    private boolean setAwait;
    private boolean autoCreateIndex;

    private InstanceConfiguration()
    {
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

    public String getVersion()
    {
        return version;
    }

    public String getClusterName()
    {
        return clusterName;
    }

    public String getPathData()
    {
        return pathData;
    }

    public String getPathLogs()
    {
        return pathLogs;
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
                .append("id", id)
                .append("baseDir", baseDir)
                .append("httpPort", httpPort)
                .append("transportPort", transportPort)
                .append("version", version)
                .append("clusterName", clusterName)
                .append("pathData", pathData)
                .append("pathLogs", pathLogs)
                .append("pathInitScript", pathInitScript)
                .append("keepExistingData", keepExistingData)
                .append("timeout", timeout)
                .append("setAwait", setAwait)
                .append("autoCreateIndex", autoCreateIndex)
                .toString();
    }

    public static class Builder
    {

        private int id;
        private String baseDir;
        private int httpPort;
        private int transportPort;
        private String version;
        private String clusterName;
        private String pathData;
        private String pathLogs;
        private String pathInitScript;
        private boolean keepExistingData;
        private int timeout;
        private boolean setAwait;
        private boolean autoCreateIndex;

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

        public InstanceConfiguration build()
        {
            Validate.notBlank(baseDir, "The elasticsearch base directory must be defined");

            InstanceConfiguration config = new InstanceConfiguration();

            config.id = id;
            config.baseDir = baseDir;
            config.httpPort = httpPort;
            config.transportPort = transportPort;
            config.version = version;
            config.clusterName = clusterName;
            config.pathData = pathData;
            config.pathLogs = pathLogs;
            config.pathInitScript = pathInitScript;
            config.keepExistingData = keepExistingData;
            config.timeout = timeout;
            config.setAwait = setAwait;
            config.autoCreateIndex = autoCreateIndex;

            return config;
        }
    }

}

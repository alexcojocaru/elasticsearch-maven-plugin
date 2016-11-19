package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.ChainedArtifactResolver;
import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.ElasticsearchConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.PluginArtifactResolver;

/**
 * Mojo to define extra maven parameters required by the run forked mojo.
 * 
 * @author Alex Cojocaru
 */
public abstract class AbstractElasticsearchMojo
        extends AbstractElasticsearchBaseMojo
        implements ElasticsearchConfiguration
{
    /**
     * Used to look up Artifacts in the remote repository.
     */
    @Component
    protected RepositorySystem repositorySystem;

    /**
     * Used to look up Artifacts in the remote repository.
     */
    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    protected RepositorySystemSession repositorySession;

    /**
     * List of Remote Repositories used by the resolver.
     */
    @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
    protected List<RemoteRepository> remoteRepositories;

    /**
     * The version of Elasticsearch to install
     */
    @Parameter(defaultValue = "5.0.0")
    protected String version;

    /**
     * The Elasticsearch cluster name to set up; alphanumeric.
     */
    @Parameter(defaultValue = "test")
    protected String clusterName;

    /**
     * The HTTP port to use for the Elasticsearch node. If multiple nodes are to be started (see the
     * instanceCount parameter), the subsequent nodes will get subsequent port numbers (eg. 9201,
     * 9202, etc).
     */
    @Parameter(defaultValue = "9200")
    protected int httpPort;

    /**
     * The TCP transport port to use for the Elasticsearch node. If multiple nodes are to be started
     * (see the instanceCount parameter), the subsequent nodes will get subsequent port numbers (eg.
     * 9301, 9302, etc).
     */
    @Parameter(defaultValue = "9300")
    protected int transportPort;

    /**
     * The path to the data directory.
     */
    @Parameter
    protected String pathData;

    /**
     * The path to the logs directory.
     */
    @Parameter
    protected String pathLogs;

    /**
     * The path to the initialization script file to execute after Elasticsearch has started.
     */
    @Parameter
    protected String pathInitScript;

    /**
     * Whether to keep existing data (data and logs directories).
     */
    @Parameter(defaultValue = "false")
    protected boolean keepExistingData;

    /**
     * How long to wait (in seconds) for the Elasticsearch cluster to start up.
     */
    @Parameter(defaultValue = "30")
    protected int timeout;

    /**
     * Whether to block the execution once all Elasticsearch instances have started,
     * so that the maven build will not proceed to the next step. Use CTRL+C to abort the process.
     */
    @Parameter(defaultValue = "false")
    protected boolean setAwait;

    /**
     * Whether to configure the Elasticsearch cluster to auto create indexes.
     */
    @Parameter(defaultValue = "false")
    protected boolean autoCreateIndex;

    
    public RepositorySystem getRepositorySystem()
    {
        return repositorySystem;
    }

    public void setRepositorySystem(RepositorySystem repositorySystem)
    {
        this.repositorySystem = repositorySystem;
    }

    public RepositorySystemSession getRepositorySession()
    {
        return repositorySession;
    }

    public void setRepositorySession(RepositorySystemSession repositorySession)
    {
        this.repositorySession = repositorySession;
    }

    public List<RemoteRepository> getRemoteRepositories()
    {
        return remoteRepositories;
    }

    public void setRemoteRepositories(List<RemoteRepository> remoteRepositories)
    {
        this.remoteRepositories = remoteRepositories;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getClusterName()
    {
        return clusterName;
    }

    public void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }

    public int getHttpPort()
    {
        return httpPort;
    }

    public void setHttpPort(int httpPort)
    {
        this.httpPort = httpPort;
    }

    public int getTransportPort()
    {
        return transportPort;
    }

    public void setTransportPort(int transportPort)
    {
        this.transportPort = transportPort;
    }

    public String getPathData()
    {
        return pathData;
    }

    public void setPathData(String pathData)
    {
        this.pathData = pathData;
    }

    public String getPathLogs()
    {
        return pathLogs;
    }

    public void setPathLogs(String pathLogs)
    {
        this.pathLogs = pathLogs;
    }

    public String getPathInitScript()
    {
        return pathInitScript;
    }

    public void setPathInitScript(String pathInitScript)
    {
        this.pathInitScript = pathInitScript;
    }

    public boolean isKeepExistingData()
    {
        return keepExistingData;
    }

    public void setKeepExistingData(boolean keepExistingData)
    {
        this.keepExistingData = keepExistingData;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public boolean isSetAwait()
    {
        return setAwait;
    }

    public void setSetAwait(boolean setAwait)
    {
        this.setAwait = setAwait;
    }

    public boolean isAutoCreateIndex()
    {
        return autoCreateIndex;
    }

    public void setAutoCreateIndex(boolean autoCreateIndex)
    {
        this.autoCreateIndex = autoCreateIndex;
    }

    
    @Override
    public List<InstanceConfiguration> buildInstanceConfigurationList()
    {
        InstanceConfigurationUtil.validateInstanceCount(instanceCount);

        List<InstanceConfiguration> configList = new ArrayList<>();
        for (int i = 0; i < instanceCount; i++)
        {
            InstanceConfiguration config = new InstanceConfiguration.Builder()
                    .withId(i)
                    .withBaseDir(baseDir.getAbsolutePath() + i)
                    .withVersion(version)
                    .withClusterName(clusterName)
                    .withHttpPort(httpPort + i)
                    .withTransportPort(transportPort + i)
                    .withPathData(pathData)
                    .withPathLogs(pathLogs)
                    .withPathInitScript(pathInitScript)
                    .withKeepExistingData(keepExistingData)
                    .withTimeout(timeout)
                    .withSetAwait(setAwait)
                    .withAutoCreateIndex(autoCreateIndex)
                    .build();
            configList.add(config);
        }

        InstanceConfigurationUtil.validatePorts(configList);

        return configList;
    }

    @Override
    public PluginArtifactResolver buildArtifactResolver()
    {
        ChainedArtifactResolver artifactResolver = new ChainedArtifactResolver();
        artifactResolver.addPluginArtifactResolver(new MyArtifactResolver(
                repositorySystem,
                repositorySession,
                remoteRepositories,
                getLog()));
        return artifactResolver;
    }

    // TODO add support for providing pathConfig which overrides pathData and pathLogs
    // ./bin/elasticsearch -Epath.conf=/path/to/my/config/

    // TODO can I override the log4j 2 config by appending extra config to the file
    // (protected String additionalLogConfigFilePath = "";)

    // TODO redirect process output? ; log plugin output / error to file?

    // update the readme
    // unit tests
    // integration tests
}

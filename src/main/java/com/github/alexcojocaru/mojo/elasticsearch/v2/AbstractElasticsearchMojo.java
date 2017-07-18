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
    @Parameter(property="es.version", defaultValue = "5.0.0")
    protected String version;

    /**
     * The Elasticsearch cluster name to set up; alphanumeric.
     */
    @Parameter(property="es.clusterName", defaultValue = "test")
    protected String clusterName;

    /**
     * The HTTP port to use for the Elasticsearch node. If multiple nodes are to be started (see the
     * instanceCount parameter), the subsequent nodes will get subsequent port numbers (eg. 9201,
     * 9202, etc).
     */
    @Parameter(property="es.httpPort", defaultValue = "9200")
    protected int httpPort;

    /**
     * The TCP transport port to use for the Elasticsearch node. If multiple nodes are to be started
     * (see the instanceCount parameter), the subsequent nodes will get subsequent port numbers (eg.
     * 9301, 9302, etc).
     */
    @Parameter(property="es.transportPort", defaultValue = "9300")
    protected int transportPort;

    /**
     * The path to the configuration directory (containing elasticsearch.yml, log4j2.properties, ...).
     */
    @Parameter(property="es.pathConf")
    protected String pathConf;

    /**
     * The path to the data directory.
     */
    @Parameter(property="es.pathData")
    protected String pathData;

    /**
     * The path to the logs directory.
     */
    @Parameter(property="es.pathLogs")
    protected String pathLogs;

    /**
     * The path to the scripts directory.
     */
    @Parameter(property="es.pathScripts")
    protected String pathScripts;
    
    /**
     * The list of plugins to install into each Elasticsearch instance.
     */
    @Parameter
    protected ArrayList<PluginConfiguration> plugins = new ArrayList<>();

    /**
     * The path to the initialization script file to execute after Elasticsearch has started.
     */
    @Parameter(property="es.pathInitScript")
    protected String pathInitScript;

    /**
     * Whether to keep existing data (data and logs directories).
     */
    @Parameter(property="es.keepExistingData", defaultValue = "false")
    protected boolean keepExistingData;

    /**
     * How long to wait (in seconds) for the Elasticsearch cluster to start up.
     */
    @Parameter(property="es.timeout", defaultValue = "30")
    protected int timeout;

    /**
     * Whether to block the execution once all Elasticsearch instances have started,
     * so that the maven build will not proceed to the next step. Use CTRL+C to abort the process.
     */
    @Parameter(property="es.setAwait", defaultValue = "false")
    protected boolean setAwait;

    /**
     * Whether to configure the Elasticsearch cluster to auto create indexes.
     */
    @Parameter(property="es.autoCreateIndex", defaultValue = "true")
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

    public String getPathConf()
    {
        return pathConf;
    }

    public void setPathConf(String pathData)
    {
        this.pathConf = pathData;
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

    public String getPathScripts()
    {
        return pathScripts;
    }

    public void setPathScripts(String pathScripts)
    {
        this.pathScripts = pathScripts;
    }

    public ArrayList<PluginConfiguration> getPlugins()
    {
        return plugins;
    }

    public void setPlugins(ArrayList<PluginConfiguration> plugins)
    {
        this.plugins = plugins;
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
    public ClusterConfiguration buildClusterConfiguration()
    {
        ClusterConfiguration.Builder clusterConfigBuilder = new ClusterConfiguration.Builder()
                .withArtifactResolver(buildArtifactResolver())
                .withLog(getLog())
                .withVersion(version)
                .withClusterName(clusterName)
                .withPathConf(pathConf)
                .withPathScripts(pathScripts)
                .withElasticsearchPlugins(plugins)
                .withPathInitScript(pathInitScript)
                .withKeepExistingData(keepExistingData)
                .withTimeout(timeout)
                .withSetAwait(setAwait)
                .withAutoCreateIndex(autoCreateIndex);

        for (int i = 0; i < instanceCount; i++)
        {
            clusterConfigBuilder.addInstanceConfiguration(new InstanceConfiguration.Builder()
                    .withId(i)
                    .withBaseDir(baseDir.getAbsolutePath() + i)
                    .withHttpPort(httpPort + i)
                    .withTransportPort(transportPort + i)
                    .withPathData(pathData)
                    .withPathLogs(pathLogs)
                    .build());
        }
        
        ClusterConfiguration clusterConfig = clusterConfigBuilder.build();
        
        return clusterConfig;
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
}

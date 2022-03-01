package com.github.alexcojocaru.mojo.elasticsearch.v2;

import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.ChainedArtifactResolver;
import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.ElasticsearchConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.PluginArtifactInstaller;
import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.PluginArtifactResolver;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * The flavour of Elasticsearch to install (default, oss).
     */
    @Parameter(property="es.flavour", defaultValue = "")
    protected String flavour;

    /**
     * The version of Elasticsearch to install
     */
    @Parameter(property="es.version", defaultValue = "5.0.0")
    protected String version;

    /**
     * The Elasticsearch download URL
     */
    @Parameter(property="es.downloadUrl", defaultValue = "")
    protected String downloadUrl;

    /**
     * The Elasticsearch download URL Username
     */
    @Parameter(property="es.downloadUrlUsername", defaultValue = "")
    protected String downloadUrlUsername;

    /**
     * The Elasticsearch download URL Password
     */
    @Parameter(property="es.downloadUrlPassword", defaultValue = "")
    protected String downloadUrlPassword;

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
     * The list of plugins to install into each Elasticsearch instance.
     */
    @Parameter
    protected ArrayList<PluginConfiguration> plugins = new ArrayList<>();

    /**
     * List of custom instance settings, applied to each instance up to `instanceCount` via the
     * `-E` commandline argument. Extra entries are ignored.
     */
    @Parameter
    protected ArrayList<Properties> instanceSettings = new ArrayList<>();

    /**
     * The path to the initialization script files to execute after Elasticsearch has started.
     * Comma-separated list
     */
    @Parameter(property="es.pathInitScript")
    protected String pathInitScript;

    /**
     * Custom environment variables, to be set before launching an instance.
     * Allows to set `JAVA_HOME` in particular.
     */
    @Parameter
    protected Map<String, String> environmentVariables = new HashMap<>();

    /**
     * Whether to keep existing data (data and logs directories).
     */
    @Parameter(property="es.keepExistingData", defaultValue = "true")
    protected boolean keepExistingData;

    /**
     * How long to wait (in seconds) for each Elasticsearch instance to start up.
     */
    @Parameter(property="es.instanceStartupTimeout", defaultValue = "120")
    protected int instanceStartupTimeout;

    /**
     * How long to wait (in seconds) for the Elasticsearch cluster to form.
     */
    @Parameter(property="es.clusterStartupTimeout", defaultValue = "30")
    protected int clusterStartupTimeout;

    /**
     * The default socket timeout (in milliseconds) for requests sent to the Elasticsearch server.
     */
    @Parameter(property="es.clientSocketTimeout", defaultValue = "5000")
    protected int clientSocketTimeout;

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

    public ArrayList<PluginConfiguration> getPlugins()
    {
        return plugins;
    }

    public void setPlugins(ArrayList<PluginConfiguration> plugins)
    {
        this.plugins = plugins;
    }

    public List<String> getPathInitScript()
    {
        if (StringUtils.isNotBlank(pathInitScript))
        {
            return Stream.of(pathInitScript.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        else
        {
            return new ArrayList<>();
        }
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

    public int getInstanceStartupTimeout()
    {
        return instanceStartupTimeout;
    }

    public void setInstanceStartupTimeout(int instanceStartupTimeout)
    {
        this.instanceStartupTimeout = instanceStartupTimeout;
    }

    public int getClusterStartupTimeout()
    {
        return clusterStartupTimeout;
    }

    public void setClusterStartupTimeout(int clusterStartupTimeout)
    {
        this.clusterStartupTimeout = clusterStartupTimeout;
    }

    public int getClientSocketTimeout() {
        return clientSocketTimeout;
    }

    public void setClientSocketTimeout(int clientSocketTimeout) {
        this.clientSocketTimeout = clientSocketTimeout;
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
        Preconditions.checkState((downloadUrlUsername == null) == (downloadUrlPassword == null), "both username and password must be supplied");
        ClusterConfiguration.Builder clusterConfigBuilder = new ClusterConfiguration.Builder()
                .withArtifactResolver(buildArtifactResolver())
                .withArtifactInstaller(buildArtifactInstaller())
                .withLog(getLog())
                .withFlavour(flavour)
                .withVersion(version)
                .withDownloadUrl(downloadUrl)
                .withDownloadUrlUsername(downloadUrlUsername)
                .withDownloadUrlPassword(downloadUrlPassword)
                .withClusterName(clusterName)
                .withPathConf(pathConf)
                .withElasticsearchPlugins(plugins)
                .withPathInitScripts(getPathInitScript())
                .withKeepExistingData(keepExistingData)
                .withStartupTimeout(clusterStartupTimeout)
                .withSetAwait(setAwait)
                .withAutoCreateIndex(autoCreateIndex);

        for (int i = 0; i < instanceCount; i++)
        {
            final Properties settings = instanceSettings.size() > i ? instanceSettings.get(i): null;
            clusterConfigBuilder.addInstanceConfiguration(new InstanceConfiguration.Builder()
                    .withId(i)
                    .withBaseDir(baseDir.getAbsolutePath() + i)
                    .withHttpPort(httpPort + i)
                    .withTransportPort(transportPort + i)
                    .withPathData(pathData)
                    .withPathLogs(pathLogs)
                    .withEnvironmentVariables(environmentVariables)
                    .withSettings(settings)
                    .withStartupTimeout(instanceStartupTimeout)
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

    public  PluginArtifactInstaller buildArtifactInstaller()
    {
        return new MyArtifactInstaller(repositorySystem, repositorySession, getLog());
    }
}

package com.github.alexcojocaru.mojo.elasticsearch.v2.step.resolveartifact;

import java.io.File;
import java.io.IOException;

import org.awaitility.core.ConditionTimeoutException;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchArtifact;
import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.ArtifactException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.FilesystemUtil;

/**
 * Resolver for the Elasticsearch artifact.
 * <br><br>
 * This class is not thread safe.
 *
 * @author Alex Cojocaru
 *
 */
public class ElasticsearchArtifactResolver
{
    private final ClusterConfiguration config;

    private final ElasticsearchArtifact artifactReference;


    public ElasticsearchArtifactResolver(ClusterConfiguration config)
    {
        this.config = config;

        this.artifactReference = new ElasticsearchArtifact
                .ElasticsearchArtifactBuilder()
                .withClusterConfig(config)
                .build();
        config.getLog().debug("Artifact ref: " + artifactReference);
    }

    /**
     * Resolve the Elasticsearch reference, downloading and installing it if necessary.
     * @return the Elasticsearch artifact file in the local filesystem
     * @throws ArtifactException when an artifact exception occurs
     * @throws IOException when an IO exception occurs
     */
    public File resolve() throws ArtifactException, IOException
    {
        return resolve(true);
    }

    /**
     * Resolve the Elasticsearch reference, downloading and installing it if necessary.
     * @param retry whether to retry on various failures
     *     (eg. time out waiting for the master process to write the server port in the lock file,
     *     time out waiting for the master process to clean up the lock file,
     *     etc)
     * @return the Elasticsearch artifact file in the local filesystem
     * @throws ArtifactException when an artifact exception occurs
     * @throws IOException when an IO exception occurs
     */
    private File resolve(boolean retryOnFail) throws ArtifactException, IOException
    {
        try
        {
            return resolveMavenArtifact();
        }
        catch (ArtifactException e)
        {
            config.getLog().info("Artifact not found; going the hard way (download and install)");

            File lockFile = buildLockFile();
            boolean lockFileCreated = lockFile.createNewFile();

            if (lockFileCreated)
            {
                config.getLog().info("Running in master mode. Created the lock file.");

                // set it to delete on exit only if we're in master mode
                lockFile.deleteOnExit();

                try
                {
                    new ElasticsearchArtifactResolverMaster(config, artifactReference)
                            .resolve(lockFile);
                }
                finally
                {
                    lockFile.delete();
                }
            }
            else
            {
                config.getLog().info("Running in slave mode. The lock file already exists.");

                ElasticsearchArtifactResolverSlave resolverSlave =
                        new ElasticsearchArtifactResolverSlave(config);

                // read the port from the lock file
                int serverPort;
                try
                {
                    serverPort = resolverSlave.readPort(lockFile);
                }
                catch (ConditionTimeoutException e1)
                {
                    return cleanupLockFileAndRetry(lockFile, retryOnFail);
                }

                if (serverPort == -1)
                {
                    config.getLog().info(
                            "The master process has finished downloading"
                            + " and installing the artifact");

                    return resolveMavenArtifact();
                }

                resolverSlave.waitForMasterResolverServer(serverPort);

                try
                {
                    resolverSlave.waitForLockFileCleanup(lockFile);
                }
                catch (ConditionTimeoutException ex)
                {
                    return cleanupLockFileAndRetry(lockFile, retryOnFail);
                }
            }

            // if we got this far, it means that the artifact was downloaded and installed
            return resolveMavenArtifact();
        }
    }

    /**
     * Build the global lock file reference. Do not attempt to resolve it against the filesystem.
     * @return the lock file reference
     */
    private File buildLockFile()
    {
        String esFilename = artifactReference.buildBundleFilename();
        String lockFilename = "elasticsearch-maven-plugin_" + esFilename + ".lock";
        File lockFile = new File(FilesystemUtil.getTempDirectory(), lockFilename);

        config.getLog().debug("Using lock file '" + lockFile.getAbsolutePath() + "'");

        return lockFile;
    }

    /**
     * Remove the lock file, and retry the artifact resolution.
     * @throws ArtifactException when an artifact exception occurs
     * @throws IOException when an IO exception occurs
     */
    private File cleanupLockFileAndRetry(File lockFile, boolean retryOnFail)
            throws ArtifactException, IOException
    {
        config.getLog().info("Presume that the lock file was left over; delete it and retry");

        if (lockFile.delete() == false)
        {
            throw new RuntimeException(
                    "The lock file '" + lockFile.getAbsolutePath()
                    + "' doesn't seem to be in use, yet we failed to remove it");
        }

        if (retryOnFail == true)
        {
            return resolve(false);
        }
        else
        {
            throw new RuntimeException(
                    "The final attempt to resolve the Elasticsearch artifact has failed");
        }
    }

    /**
     * Resolve the given artifact reference against the local maven repository.
     * @return the maven artifact file in the local filesystem
     * @throws ArtifactException when an artifact exception occurs
     */
    private File resolveMavenArtifact() throws ArtifactException
    {
        config.getLog().debug("Resolving artifact against the local maven repo");

        return config
                .getArtifactResolver()
                .resolveArtifact(artifactReference.getArtifactCoordinates());
    }
}

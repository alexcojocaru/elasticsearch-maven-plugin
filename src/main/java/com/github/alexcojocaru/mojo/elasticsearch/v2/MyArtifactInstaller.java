package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.File;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallationException;

import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.ArtifactException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.PluginArtifactInstaller;

/**
 * @author Alex Cojocaru
 */
public class MyArtifactInstaller
        implements PluginArtifactInstaller
{
    private final RepositorySystem repositorySystem;
    private final RepositorySystemSession repositorySession;
    private final Log log;

    public MyArtifactInstaller(RepositorySystem repositorySystem,
            RepositorySystemSession repositorySession,
            Log log)
    {
        this.repositorySystem = repositorySystem;
        this.repositorySession = repositorySession;
        this.log = log;
    }

    @Override
    public void installArtifact(ElasticsearchArtifact artifact, File file) throws ArtifactException
    {
        log.debug("Installing '" + file.getAbsolutePath() + "' in the local maven repo");

        InstallRequest request = new InstallRequest();
        Artifact defaultArtifact = new DefaultArtifact(
                artifact.getGroupId(),
                artifact.getArtifactId(),
                artifact.getClassifier(),
                artifact.getType(),
                artifact.getVersion(),
                null,
                file);
        request.addArtifact(defaultArtifact);

        log.info(String.format("Installing maven artifact: %s", artifact));

        try
        {
            repositorySystem.install(repositorySession, request);
        }
        catch (InstallationException e) {
            throw new ArtifactException(e.getMessage(), e);
        }
    }
}

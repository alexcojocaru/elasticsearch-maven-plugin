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
    public void installArtifact(
            String groupId,
            String artifactId,
            String version,
            String classifier,
            String extension,
            File file)
            throws ArtifactException
    {
        InstallRequest request = new InstallRequest();
        Artifact artifact = new DefaultArtifact(
                groupId,
                artifactId,
                classifier,
                extension,
                version,
                null,
                file);
        request.addArtifact(artifact);
        
        log.debug(String.format("Installing artifact: %s", artifact));

        try
        {
            repositorySystem.install(repositorySession, request);
        }
        catch (InstallationException e) {
            throw new ArtifactException(e.getMessage(), e);
        }
    }
}

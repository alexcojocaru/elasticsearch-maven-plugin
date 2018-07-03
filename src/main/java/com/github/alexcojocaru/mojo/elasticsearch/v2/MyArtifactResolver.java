/**
 * Copyright (C) 2010-2012 Joerg Bellmann <joerg.bellmann@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.PluginArtifactResolver;
import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.ArtifactException;

/**
 * Copied from the t7mp project.
 * Uses Maven-API to resolve the Artifacts.
 */
public class MyArtifactResolver
        implements PluginArtifactResolver
{
    private final RepositorySystem repositorySystem;
    private final RepositorySystemSession repositorySession;
    private final List<RemoteRepository> remoteRepositories;
    private final Log log;

    public MyArtifactResolver(RepositorySystem repositorySystem,
            RepositorySystemSession repositorySession,
            List<RemoteRepository> remoteRepositories,
            Log log)
    {
        this.repositorySystem = repositorySystem;
        this.repositorySession = repositorySession;
        this.remoteRepositories = remoteRepositories;
        this.log = log;
    }

    /**
     * Resolves an Artifact from the repositories.
     * 
     * @param coordinates The artifact coordinates
     * @return The local file resolved/downloaded for the given coordinates
     * @throws ArtifactException If the artifact cannot be resolved
     */
    @Override
    public File resolveArtifact(String coordinates) throws ArtifactException
    {
        ArtifactRequest request = new ArtifactRequest();
        Artifact artifact = new DefaultArtifact(coordinates);
        request.setArtifact(artifact);
        request.setRepositories(remoteRepositories);

        log.debug(String.format("Resolving artifact %s from %s", artifact, remoteRepositories));

        ArtifactResult result;
        try
        {
            result = repositorySystem.resolveArtifact(repositorySession, request);
        }
        catch (ArtifactResolutionException e)
        {
            throw new ArtifactException(e.getMessage(), e);
        }

        log.debug(String.format("Resolved artifact %s to %s from %s",
                artifact,
                result.getArtifact().getFile(),
                result.getRepository()));

        return result.getArtifact().getFile();
    }
}

package com.github.alexcojocaru.mojo.elasticsearch.v2.configuration;

import java.io.File;

/**
 * @author Alex Cojocaru
 *
 */
public interface PluginArtifactInstaller
{

    void installArtifact(
            String groupId,
            String artifactId,
            String version,
            String classifier,
            String extension,
            File file)
            throws ArtifactException;

}

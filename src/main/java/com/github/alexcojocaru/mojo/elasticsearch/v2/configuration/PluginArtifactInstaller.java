package com.github.alexcojocaru.mojo.elasticsearch.v2.configuration;

import java.io.File;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchArtifact;

/**
 * @author Alex Cojocaru
 *
 */
public interface PluginArtifactInstaller
{

    void installArtifact(ElasticsearchArtifact artifact, File file) throws ArtifactException;

}

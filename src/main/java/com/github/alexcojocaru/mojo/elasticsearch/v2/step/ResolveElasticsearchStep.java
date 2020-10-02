package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.ArtifactException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.step.resolveartifact.ElasticsearchArtifactResolver;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.ArchiveUtil;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.FilesystemUtil;

/**
 * Download and unpack elasticsearch into the destination directory.
 *
 * @author Alex Cojocaru
 */
public class ResolveElasticsearchStep
        implements InstanceStep
{
    @Override
    public void execute(InstanceConfiguration config)
    {
        File unpackDirectory = null;
        try
        {
            File artifact = new ElasticsearchArtifactResolver(config.getClusterConfiguration())
                    .resolve();

            unpackDirectory = unpackToElasticsearchDirectory(artifact, config);

            setupElasticsearchConf(config);
        }
        catch (ArtifactException | IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            cleanUp(unpackDirectory, config.getClusterConfiguration());
        }
    }

    private void cleanUp(File unpackDirectory, ClusterConfiguration config)
    {
        if (unpackDirectory != null)
        {
            try
            {
                FileUtils.deleteDirectory(unpackDirectory);
            }
            catch (IOException e)
            {
                config.getLog().error(
                        String.format(
                            "Could not delete Elasticsearch unpack directory : ",
                            unpackDirectory.getAbsolutePath()),
                        e);
            }
        }
    }

    private void setupElasticsearchConf(InstanceConfiguration config) throws IOException
    {
        String pathConf = config.getClusterConfiguration().getPathConf();
        if (pathConf != null && !pathConf.isEmpty())
        {
            // Merge the user-defined config directory with the default one
            // This allows user to omit some configuration files (jvm.options for instance)
            File baseDir = new File(config.getBaseDir());
            FileUtils.copyDirectory(new File(pathConf), new File(baseDir, "config"));
        }
    }

    private File unpackToElasticsearchDirectory(File artifact, InstanceConfiguration config)
            throws IOException
    {
        File unpackDirectory = getUnpackDirectory();
        ArchiveUtil.autodetectAndExtract(artifact, unpackDirectory);
        File baseDir = new File(config.getBaseDir());
        moveToElasticsearchDirectory(unpackDirectory, baseDir);

        return unpackDirectory;
    }

    private void moveToElasticsearchDirectory(File src, File dest) throws IOException
    {
        File[] files = src.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(final File file)
            {
                return file.isDirectory();
            }
        });

        // should only be one
        FilesystemUtil.copyRecursively(files[0].toPath(), dest.toPath());
    }

    protected File getUnpackDirectory()
    {
        File tempDir = FilesystemUtil.getTempDirectory();
        File upackDirectory = new File(tempDir, UUID.randomUUID().toString());
        upackDirectory.mkdirs();
        return upackDirectory;
    }
}

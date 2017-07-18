package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.UUID;

import org.apache.maven.plugin.logging.Log;
import org.zeroturnaround.zip.ZipUtil;
import org.zeroturnaround.zip.commons.FileUtils;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchArtifact;
import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.ResolutionException;

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
        Log log = config.getClusterConfiguration().getLog();
        
        File unpackDirectory = null;
        try
        {
            String version = config.getClusterConfiguration().getVersion();

            ElasticsearchArtifact artifact = new ElasticsearchArtifact(version);

            log.debug("Resolving " + artifact.toString());

            File resolvedArtifact = config.getClusterConfiguration()
                    .getArtifactResolver()
                    .resolveArtifact(artifact.getArtifactCoordinates());
            unpackDirectory = getUnpackDirectory();
            ZipUtil.unpack(resolvedArtifact, unpackDirectory);
            File baseDir = new File(config.getBaseDir());
            moveToElasticsearchDirectory(unpackDirectory, baseDir);

            String pathConf = config.getClusterConfiguration().getPathConf();
            if (pathConf != null && !pathConf.isEmpty())
            {
                // Merge the user-defined config directory with the default one
                // This allows user to omit some configuration files (jvm.options for instance)
                FileUtils.copyDirectory(new File(pathConf), new File(baseDir, "config"));
            }
        }
        catch (ResolutionException | IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if (unpackDirectory != null)
            {
                try
                {
                    FileUtils.deleteDirectory(unpackDirectory);
                }
                catch (IOException e)
                {
                    log.error(
                            String.format(
                                "Could not delete Elasticsearch upack directory : ",
                                unpackDirectory.getAbsolutePath()),
                            e);
                }
            }
        }
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
        FileUtils.copyDirectory(files[0], dest);
    }

    protected File getUnpackDirectory()
    {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File upackDirectory = new File(tempDir, UUID.randomUUID().toString());
        upackDirectory.mkdirs();
        return upackDirectory;
    }

}

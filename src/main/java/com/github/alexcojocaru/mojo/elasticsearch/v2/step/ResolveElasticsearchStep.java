package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.zeroturnaround.zip.ZipUtil;
import org.zeroturnaround.zip.commons.FileUtils;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchArtifact;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceContext;
import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.ResolutionException;

/**
 * Download and unpack elasticsearch into the destination directory.
 *
 * @author Alex Cojocaru
 */
public class ResolveElasticsearchStep
        implements Step
{
    @Override
    public void execute(InstanceContext context)
    {
        if (StringUtils.isEmpty(context.getConfiguration().getVersion()))
        {
            throw new ElasticsearchSetupException("Version should not be null or empty.");
        }

        File unpackDirectory = null;
        try
        {
            String version = context.getConfiguration().getVersion();
            ElasticsearchArtifact artifact = new ElasticsearchArtifact(version);

            context.getLog().debug("Resolving " + artifact.toString());

            File resolvedArtifact = context.getArtifactResolver()
                    .resolveArtifact(artifact.getArtifactCoordinates());
            unpackDirectory = getUnpackDirectory();
            ZipUtil.unpack(resolvedArtifact, unpackDirectory);
            moveToElasticsearchDirectory(
                    unpackDirectory,
                    new File(context.getConfiguration().getBaseDir()));
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
                    context.getLog().error("Could not delete Elasticsearch upack directory : "
                            + unpackDirectory.getAbsolutePath());
                    context.getLog().error(e.getMessage(), e);
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

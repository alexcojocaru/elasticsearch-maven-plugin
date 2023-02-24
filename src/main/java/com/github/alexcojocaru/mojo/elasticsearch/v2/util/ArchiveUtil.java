package com.github.alexcojocaru.mojo.elasticsearch.v2.util;

import java.io.File;

import org.codehaus.plexus.archiver.AbstractUnArchiver;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

public class ArchiveUtil
{

    public static void extract(File archiveFile, File targetDir)
    {
        AbstractUnArchiver unArchiver;

        String filename = archiveFile.getName().toLowerCase();
        if (filename.endsWith(".zip"))
        {
            unArchiver = new ZipUnArchiver(archiveFile);
        }
        else if (filename.endsWith(".tar.gz"))
        {
            unArchiver = new TarGZipUnArchiver(archiveFile);
        }
        else
        {
            throw new IllegalArgumentException(
                    "Unknown archive type for file: " + archiveFile.getPath());
        }

        unArchiver.setDestDirectory(targetDir);
        unArchiver.extract();
    }

}

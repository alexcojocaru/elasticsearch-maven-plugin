package com.github.alexcojocaru.mojo.elasticsearch.v2.util;

import java.io.File;
import java.util.Locale;

import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.zeroturnaround.zip.ZipUtil;

public class ArchiveUtil
{

    public static void extract(File archiveFile, File targetDir)
    {
        String filename = archiveFile.getPath().toLowerCase(Locale.ROOT);
        if (filename.endsWith(".zip"))
        {
            ZipUtil.unpack(archiveFile, targetDir);
        }
        else if (filename.endsWith(".tar.gz"))
        {
            TarGZipUnArchiver unArchiver = new TarGZipUnArchiver();
            unArchiver.setDestDirectory(targetDir);
            unArchiver.extract(archiveFile.getPath(), targetDir); // the 2nd arg is not used :-o
        }
        else
        {
            throw new IllegalArgumentException(
                    "Unknown archive type for file name: " + archiveFile.getName());
        }
    }

}

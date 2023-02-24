package com.github.alexcojocaru.mojo.elasticsearch.v2.util;

import java.io.File;
import java.io.IOException;

import org.rauschig.jarchivelib.ArchiverFactory;

public class ArchiveUtil
{

    public static void extract(File archiveFile, File targetDir) throws IOException
    {
        ArchiverFactory.createArchiver(archiveFile).extract(archiveFile, targetDir);
    }

}

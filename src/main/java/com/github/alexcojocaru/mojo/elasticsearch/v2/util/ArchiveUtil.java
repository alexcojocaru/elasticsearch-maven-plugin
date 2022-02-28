/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.github.alexcojocaru.mojo.elasticsearch.v2.util;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.zeroturnaround.zip.ZipUtil;

public class ArchiveUtil {

    public static void autodetectAndExtract(File archiveFile, File targetDir) throws IOException {
        String filename = archiveFile.toString().toLowerCase(Locale.ROOT);
        if (filename.endsWith(".zip"))
        {
            extractZip(archiveFile, targetDir);
        }
        else if (filename.endsWith(".tar.gz"))
        {
            extractTarGz(archiveFile, targetDir);
        }
        else {
            throw new IOException("Unsupported archive format for " + archiveFile);
        }
    }

    public static void extractZip(File archiveFile, File targetDir) {
        ZipUtil.unpack(archiveFile, targetDir);
    }

    public static void extractTarGz(File archiveFile, File targetDir) {
        TarGZipUnArchiver unArchiver = new TarGZipUnArchiver(archiveFile);
        unArchiver.setDestDirectory(targetDir);
        // We don't want logging, but this is necessary to avoid an NPE
        unArchiver.enableLogging(new ConsoleLogger(ConsoleLogger.LEVEL_DISABLED, "console"));
        unArchiver.extract();
    }

}

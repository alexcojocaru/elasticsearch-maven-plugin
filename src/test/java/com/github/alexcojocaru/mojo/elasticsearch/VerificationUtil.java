package com.github.alexcojocaru.mojo.elasticsearch;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Alex Cojocaru
 */
public class VerificationUtil
{
    /**
     * Verify that the given baseDir is a directory on the disk.
     * 
     * @param baseDir
     * @throws FileNotFoundException
     */
    public static void verifyBaseDirectory(File baseDir) throws FileNotFoundException
    {
        if (!baseDir.isDirectory())
        {
            throw new FileNotFoundException(baseDir.getAbsolutePath());
        }
    }

    /**
     * Verify that the ES instance in the given base directory is not running
     * 
     * @param baseDir
     * @throws IllegalStateException
     */
    public static void verifyInstanceIsNotRunning(File baseDir) throws IllegalStateException
    {
        // the instance creates a " pid" file on start, check that the file doesn't exist
        File pidFile = new File(baseDir, " pid");
        if (pidFile.exists())
        {
            throw new IllegalStateException("The 'pid' file exists: " + pidFile.getAbsolutePath()
                    + "; it means that the ES instance is still running");
        }
    }

}

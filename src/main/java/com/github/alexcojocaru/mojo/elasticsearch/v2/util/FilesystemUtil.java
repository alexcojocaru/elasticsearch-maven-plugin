package com.github.alexcojocaru.mojo.elasticsearch.v2.util;

import java.io.File;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang3.SystemUtils;

import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;

/**
 * Several utilities to help in dealing with the elasticsearch file structure.
 * 
 * @author Alex Cojocaru
 */
public final class FilesystemUtil
{

    private FilesystemUtil()
    {
        // hide constructor
    }

    public static File getBinDirectory(File base)
    {
        return new File(base, "/bin/");
    }

    public static File getDataDirectory(File base)
    {
        return new File(base, "/data/");
    }

    public static File getLogsDirectory(File base)
    {
        return new File(base, "/logs/");
    }

    /**
     * Set the 755 permissions on the given script.
     * @param config - the instance config
     * @param scriptName - the name of the script (located in the bin directory) to make executable
     */
    public static void setScriptPermission(InstanceConfiguration config, String scriptName)
    {
        if (SystemUtils.IS_OS_WINDOWS)
        {
            // we do not have file permissions on windows
            return;
        }
        
        CommandLine command = new CommandLine("chmod")
                .addArgument("755")
                .addArgument(String.format("bin/%s", scriptName));
        ProcessUtil.executeScript(config, command);
    }
}

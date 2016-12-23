package com.github.alexcojocaru.mojo.elasticsearch.v2.util;

import java.io.File;

/**
 * Several utilities to help in dealing with the elasticsearch file structure.
 * 
 * @author Alex Cojocaru
 */
public final class ElasticsearchUtil
{

    private ElasticsearchUtil()
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
}

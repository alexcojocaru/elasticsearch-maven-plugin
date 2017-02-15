/***************************************************************************
 * Copyright (C) 2017 Ping Identity Corporation
 * All rights reserved.
 * 
 * The contents of this file are the property of Ping Identity Corporation.
 * You may not copy or use this file, in either source code or executable
 * form, except in compliance with terms set by Ping Identity Corporation.
 * For further information please contact:
 * 
 *     Ping Identity Corporation
 *     1001 17th Street Suite 100
 *     Denver, CO 80202
 *     303.468.2900
 *     http://www.pingidentity.com
 * 
 **************************************************************************/
package com.github.alexcojocaru.mojo.elasticsearch.v2.util;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.logging.Log;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;

/**
 * @author acojocaru@pingidentity.com
 *
 * TODO: use https://commons.apache.org/proper/commons-exec/ as abstraction layer
 */
public class ProcessUtil
{
    /**
     * Run the given command within the supplied instance config as a process and wait
     * until it finalizes.
     * @param config - the instance config
     * @param command - the command to execute
     * @param shutdownHook - a shutdown hook builder,
     * to attach a shut down hook to the process being started; can be null
     */
    public static void executeScript(InstanceConfiguration config, String[] command,
            ShutdownHook shutdownHook)
    {
        Log log = config.getClusterConfiguration().getLog();
        int instanceId = config.getId();
        File baseDir = new File(config.getBaseDir());
        
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(baseDir);
        processBuilder.inheritIO();
        
        int exitCode = -1;

        try
        {
            log.info(String.format(
                    "Elasticsearch[%d]: Executing command '%s' in directory '%s'",
                    instanceId,
                    String.join(" ", command),
                    baseDir));

            Process process = processBuilder.start();
            if (shutdownHook != null)
            {
                shutdownHook.attachShutdownHook(process);
            }

            exitCode = process.waitFor();
            
            if (exitCode != 0)
            {
                throw new ElasticsearchSetupException(String.format(
                        "Elasticsearch [%d]: Command '%s' in directory '%s' finished with exit code %d; see above for details",
                        instanceId, String.join(" ", command), baseDir, exitCode));
            }
            
            log.info(String.format(
                    "Elasticsearch[%d]: The process finished with exit code %d",
                    instanceId,
                    exitCode));
        }
        catch (InterruptedException | IOException e)
        {
            throw new ElasticsearchSetupException(
                    String.format(
                    "Elasticsearch [%d]: Cannot execute command '%s' in directory '%s'",
                    instanceId, String.join(" ", command), baseDir),
                    e);
        }
    }

}

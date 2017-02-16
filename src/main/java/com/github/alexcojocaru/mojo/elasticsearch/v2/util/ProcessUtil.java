package com.github.alexcojocaru.mojo.elasticsearch.v2.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ProcessDestroyer;
import org.apache.commons.exec.environment.EnvironmentUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.logging.Log;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;

/**
 * @author Alex Cojocaru
 */
public class ProcessUtil
{
    /**
     * Build an OS dependent command line around the given executable name / relative path.
     * On Windows, the '/' file path separator in the executable are replaced with '\'.
     * @param executable - the executable name or relative path
     * @return - the command line tailored to the current OS
     */
    public static CommandLine buildCommandLine(String executable)
    {
        CommandLine cmd;

        if (SystemUtils.IS_OS_WINDOWS)
        {
            String windowsExecutable = executable.replace('/', '\\');
            cmd = new CommandLine("cmd").addArgument(String.format("/c %s", windowsExecutable));
        }
        else
        {
            cmd = new CommandLine(executable);
        }
        
        return cmd;
    }
    
    /**
     * Run the given command as a process within the supplied instance config context
     * and wait until it finalizes. An ElasticsearchSetupException is thrown if the exit code
     * is not 0.
     * @param config - the instance config
     * @param command - the command to execute
     */
    public static void executeScript(InstanceConfiguration config, CommandLine command)
    {
        executeScript(config, command, null, null);
    }
    
    /**
     * Run the given command as a process within the supplied instance config context
     * and wait until it finalizes. An ElasticsearchSetupException is thrown if the exit code
     * is not 0.
     * @param config - the instance config
     * @param command - the command to execute
     * @param environment - a map of environment variables; can be null
     * @param processDestroyer - a destroyer handler for the spawned process; can be null 
     */
    public static void executeScript(InstanceConfiguration config, CommandLine command,
            Map<String, String> environment, ProcessDestroyer processDestroyer)
    {
        Log log = config.getClusterConfiguration().getLog();
        int instanceId = config.getId();
        File baseDir = new File(config.getBaseDir()); 
        
        Map<String, String> completeEnvironment = createEnvironment(environment);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(baseDir);
        executor.setProcessDestroyer(processDestroyer); // allows null

        try
        {
            log.debug(String.format("Using environment: %s", completeEnvironment));
            
            log.info(String.format(
                    "Elasticsearch[%d]: Executing command '%s' in directory '%s'",
                    instanceId,
                    command.toString(),
                    baseDir));

            int exitCode = executor.execute(command, completeEnvironment);
            
            if (exitCode != 0)
            {
                throw new ElasticsearchSetupException(String.format(
                        "Elasticsearch [%d]: Command '%s' in directory '%s' finished with exit code %d; see above for details",
                        instanceId, command, baseDir, exitCode));
            }
            
            log.info(String.format(
                    "Elasticsearch[%d]: The process finished with exit code %d",
                    instanceId,
                    exitCode));
        }
        catch (IOException e)
        {
            throw new ElasticsearchSetupException(
                    String.format(
                    "Elasticsearch [%d]: Cannot execute command '%s' in directory '%s'",
                    instanceId, command, baseDir),
                    e);
        }
    }

    /**
     * Create an environment by merging the current environment and the supplied one.
     * If the supplied environment is null, null is returned.
     * @param environment
     * @return an execution environment
     */
    private static Map<String, String> createEnvironment(Map<String, String> environment)
    {
        Map<String, String> result = null;
        
        if (environment != null)
        {
            try
            {
                result = EnvironmentUtils.getProcEnvironment();
            }
            catch (IOException ex)
            {
                throw new ElasticsearchSetupException(
                        "Cannot get the current process environment", ex);
            }
            result.putAll(environment);
        }
        
        return result;
    }
    

}

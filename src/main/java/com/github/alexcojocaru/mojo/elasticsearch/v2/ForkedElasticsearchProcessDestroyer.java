package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ProcessDestroyer;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.logging.Log;

import com.github.alexcojocaru.mojo.elasticsearch.v2.util.ProcessUtil;

/**
 * Shutdown hook to stop the ES process on JVM shut down.
 * 
 * @author Alex Cojocaru
 */
public class ForkedElasticsearchProcessDestroyer implements ProcessDestroyer
{
    private final Log log;
    private Process process;

    public ForkedElasticsearchProcessDestroyer(final InstanceConfiguration config)
    {
        this.log = config.getClusterConfiguration().getLog();
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                ForkedElasticsearchProcessDestroyer.this.terminateProcess(config);
            }
        });
    }
    
    @Override
    public boolean add(Process process)
    {
        if (this.process != null)
        {
            throw new IllegalStateException(
                    "A process was already added; this Elasticsearch process destroyer does not support multiple processes");
        }
        
        this.process = process;
        return true;
    }


    @Override
    public boolean remove(Process process)
    {
        throw new IllegalStateException(
                "This Elasticsearch process destroyer does not support this operation");
    }


    @Override
    public int size()
    {
        return process != null ? 1 : 0;
    }
    
    private void terminateProcess(InstanceConfiguration config)
    {
        if (SystemUtils.IS_OS_WINDOWS)
        {
            terminateWindowsProcess(config);
        }
        else
        {
            terminateUnixProcess(config);
        }
    }
    
    /*
     * On Windows, the process is started as a subprocess of the shell script.
     * Process.destroy will terminate the main process (ie. the shell),
     * but not the subprocess, therefore it cannot be used.
     * Instead, lets use the PID saved by ES into the pid file.
     */
    private void terminateWindowsProcess(InstanceConfiguration config)
    {
        log.info("Cleaning up at application shutdown...");

        String pid;
        try
        {
            pid = ProcessUtil.getElasticsearchPid(config.getBaseDir());
            log.debug("Read PID '" + pid + "' from pid file");
        }
        catch (Exception ex)
        {
            log.debug("Cannot read the PID from file; assuming the process is not running", ex);
            return;
        }
        
        boolean isAlive;
        try
        {
            isAlive = ProcessUtil.isWindowsProcessAlive(config, pid);
            log.debug("Process is still running: " + isAlive);
        }
        catch (Exception ex)
        {
            log.debug("Cannot determine if the process is running; assuming it is", ex);
            isAlive = true;
        }
        
        if (isAlive == false) {
            return;
        }
        
        log.info(String.format(
                "The Elasticsearch process [%d] is still running; stopping it...",
                config.getId()));
        
        CommandLine command = ProcessUtil.buildKillCommandLine(pid);
        try
        {
            ProcessUtil.executeScript(config, command, true);

            log.info(String.format(
                    "... the Elasticsearch process [%d] has stopped.",
                    config.getId()));
        }
        catch (Exception e)
        {
            if (e.getMessage().contains("no running instance"))
            {
                log.info(String.format(
                        "... the Elasticsearch process [%d] has stopped.",
                        config.getId()));
            }
            else
            {
                log.error(String.format(
                        "Error while destroying the Elasticsearch process [%d]",
                        config.getId()),
                        e);
            }
        }
    }
    
    private void terminateUnixProcess(InstanceConfiguration config)
    {
        log.info("Cleaning up at application shutdown...");

        if (process.isAlive())
        {
            log.info("The Elasticsearch process is still running; stopping it ...");

            process.destroy();

            try
            {
                int exitCode = process.waitFor();

                log.info(String.format(
                        "... the Elasticsearch process [%d] has stopped. Exit code: %d",
                        config.getId(),
                        exitCode));
            }
            catch (InterruptedException e)
            {
                log.error(
                        String.format(
                                "Error while waiting for the Elasticsearch process [%d] to be destroyed",
                                config.getId()),
                        e);
            }
        }
        else
        {
            log.info("The Elasticsearch process has already stopped. Nothing to clean up");
        } 
    }

}

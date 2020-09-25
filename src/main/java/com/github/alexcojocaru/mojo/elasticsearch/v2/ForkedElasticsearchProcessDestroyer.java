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
public class ForkedElasticsearchProcessDestroyer implements ProcessDestroyer, Runnable
{
    private final Log log;
    private final InstanceConfiguration config;
    private Process process;

    public ForkedElasticsearchProcessDestroyer(final InstanceConfiguration config)
    {
        this.log = config.getClusterConfiguration().getLog();
        this.config = config;
    }

    @Override
    public synchronized boolean add(Process process)
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
    public synchronized boolean remove(Process process)
    {
        if(this.process != process)
        {
            throw new IllegalStateException(
                    "Can only remove the same process that was added; this Elasticsearch process destroyer does not support multiple processes");
        }
        return true;
    }


    @Override
    public int size()
    {
        return process != null && process.isAlive() ? 1 : 0;
    }

    @Override
    public void run()
    {
        if (process != null) {
            if (SystemUtils.IS_OS_WINDOWS)
            {
                terminateWindowsProcess();
            }
            else
            {
                terminateUnixProcess();
            }
            process = null;
        }
    }

    /*
     * On Windows, the process is started as a subprocess of the shell script.
     * Process.destroy will terminate the main process (ie. the shell),
     * but not the subprocess, therefore it cannot be used.
     * Instead, lets use the PID saved by ES into the pid file.
     */
    private void terminateWindowsProcess()
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

        if (isAlive == false)
        {
            return;
        }

        log.info(String.format(
                "The Elasticsearch process [%d] is still running; stopping it...",
                config.getId()));

        CommandLine command = ProcessUtil.buildKillCommandLine(pid);
        for (int retry = 0; retry < 3; ++retry)
        {
            try
            {
                ProcessUtil.executeScript(config, command, true);

                log.info(String.format(
                        "... the Elasticsearch process [%d] has stopped.",
                        config.getId()));
                ProcessUtil.cleanupPid(config.getBaseDir());
            }
            catch (Exception e)
            {
                if (e.getMessage().contains("no running instance"))
                {
                    log.info(String.format(
                            "... the Elasticsearch process [%d] has stopped.",
                            config.getId()));
                    ProcessUtil.cleanupPid(config.getBaseDir());
                }
                else
                {
                    // sometimes taskkill fails with "The operation attempted is not supported."
                    // https://blogs.technet.microsoft.com/markrussinovich/2005/08/17/unkillable-processes/
                    // in that case, retry
                    if (retry < 2) {
                        try {
                            Thread.sleep(500);
                            continue;
                        } catch (InterruptedException e1) {
                            /* ignore */
                        }
                    }
                    log.error(String.format(
                            "Error while destroying the Elasticsearch process [%d]",
                            config.getId()),
                            e);
                }
            }
            break;
        }
    }

    private void terminateUnixProcess()
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

package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.apache.commons.exec.ProcessDestroyer;
import org.apache.maven.plugin.logging.Log;

/**
 * Shutdown hook to stop the ES process on JVM shut down.
 * 
 * @author Alex Cojocaru
 */
public class ForkedElasticsearchProcessDestroyer implements ProcessDestroyer
{
    private final Log log;
    private Process process;

    public ForkedElasticsearchProcessDestroyer(InstanceConfiguration config)
    {
        this.log = config.getClusterConfiguration().getLog();
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                if (process.isAlive())
                {
                    log.info("Stopping the Elasticsearch process at application shutdown ...");

                    process.destroy();

                    try
                    {
                        int exitCode = process.waitFor();
                        log.info(String.format(
                                "... the Elasticsearch process has stopped. Exit code: %d",
                                exitCode));
                    }
                    catch (InterruptedException e)
                    {
                        log.error("Error when waiting for the Elasticsearch process to be destroyed",
                                e);
                    }
                }
                else
                {
                    log.info("The Elasticsearch process has already stopped. Nothing to clean up");
                }
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

}

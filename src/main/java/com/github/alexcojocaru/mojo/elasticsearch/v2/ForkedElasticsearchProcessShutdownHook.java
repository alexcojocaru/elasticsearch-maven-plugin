package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.apache.maven.plugin.logging.Log;

/**
 * Shutdown hook to stop the ES process on JVM shut down.
 * 
 * @author Alex Cojocaru
 */
public class ForkedElasticsearchProcessShutdownHook
        extends Thread
{
    private final Process elasticsearchProcess;
    private final Log log;

    public ForkedElasticsearchProcessShutdownHook(Process elasticsearchProcess, Log log)
    {
        this.elasticsearchProcess = elasticsearchProcess;
        this.log = log;
    }

    @Override
    public void run()
    {
        if (elasticsearchProcess.isAlive())
        {
            log.info("Stopping the Elasticsearch process at application shutdown ...");

            elasticsearchProcess.destroy();

            try
            {
                int exitCode = elasticsearchProcess.waitFor();
                log.info("... the Elasticsearch process stopped. Exit code: " + exitCode);
            }
            catch (InterruptedException e)
            {
                log.error("Error when waiting for destroying the Elasticsearch process", e);
            }
        }
        else
        {
            log.info("The Elasticsearch process has already stopped. Nothing to clean up");
        }
    }
}

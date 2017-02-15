package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.apache.maven.plugin.logging.Log;

import com.github.alexcojocaru.mojo.elasticsearch.v2.util.ShutdownHook;

/**
 * Shutdown hook to stop the ES process on JVM shut down.
 * 
 * @author Alex Cojocaru
 */
public class ForkedElasticsearchProcessShutdownHook implements ShutdownHook
{
    private final Log log;

    public ForkedElasticsearchProcessShutdownHook(InstanceConfiguration config)
    {
        this.log = config.getClusterConfiguration().getLog();
    }
    
    @Override
    public void attachShutdownHook(final Process elasticsearchProcess)
    {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
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
                        log.info("... the Elasticsearch process has stopped. Exit code: "
                                + exitCode);
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
        });
    }



}

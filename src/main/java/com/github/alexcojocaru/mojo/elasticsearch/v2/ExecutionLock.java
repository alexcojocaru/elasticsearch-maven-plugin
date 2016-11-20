package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.apache.maven.plugin.logging.Log;

/**
 * Make the current thread wait indefinitely.
 * 
 * @author Alex Cojocaru
 */
public final class ExecutionLock
{
    private Object lock = new Object();
    private Log log;

    public ExecutionLock(Log log)
    {
        this.log = log;
    }

    /**
     * Causes the current thread to wait indefinitely. This method does not return.
     */
    public void lock()
    {
        log.info("Elasticsearch has started and the maven process has been blocked. Press CTRL+C to stop the process.");

        synchronized (lock)
        {
            try
            {
                lock.wait();
            }
            catch (InterruptedException exception)
            {
                log.warn("RunElasticsearchNodeMojo interrupted");
            }
        }
    }
}

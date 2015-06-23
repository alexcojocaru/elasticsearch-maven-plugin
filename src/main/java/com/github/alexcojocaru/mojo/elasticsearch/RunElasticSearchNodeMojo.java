package com.github.alexcojocaru.mojo.elasticsearch;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Starts a local instance of ElasticSearch indefinitely.
 * In order to kill it a CTRL+C has to be made.
 *
 * @author gfernandes
 * @goal run
 * @execute phase="compile"
 * @requiresDependencyResolution runtime
 */
public class RunElasticSearchNodeMojo extends AbstractStartElasticsearchNodeMojo {
    /**
     * @parameter
     */
    private File scriptFile;

    final private CountDownLatch waitES = new CountDownLatch(1);

    public void execute() throws MojoExecutionException {
        super.execute();
        if (scriptFile != null) {
            getLog().info("RunElasticSearchNodeMojo loading data");
            LoadElasticSearchUtility.load(scriptFile, getLog());
        }

        //Adding shutdown hook to stop ES
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                ElasticSearchNode.stop();
                waitES.countDown();
            }
        });

        waitIndefinitely();

        getLog().info("RunElasticSearchNodeMojo waiting for ES to be stopped");
        try {
            waitES.await(300, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            getLog().warn("RunElasticSearchNodeMojo interrupted while waiting for ES to be stopped");
        }
    }

    /**
     * Causes the current thread to wait indefinitely. This method does not return.
     */
    private void waitIndefinitely() {
        Object lock = new Object();

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException exception) {
                getLog().warn("RunElasticSearchNodeMojo interrupted");
            }
        }
    }

}

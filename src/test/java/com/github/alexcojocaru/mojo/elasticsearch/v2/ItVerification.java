package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.File;
import java.io.FileNotFoundException;

import com.github.alexcojocaru.mojo.elasticsearch.v2.client.Monitor;

/**
 * Used by the groovy verification scripts executed at the end of each integration test.
 * 
 * @author Alex Cojocaru
 */
public class ItVerification
{
    /**
     * The base directory of Elasticsearch (ie. the directory where ES was installed).
     */
    private final File esBaseDir;
    

    public ItVerification(File esBaseDir)
    {
        this.esBaseDir = esBaseDir;
    }
    

    /**
     * Verify that the Elasticsearch base directory is a directory on the disk.
     * 
     * @throws FileNotFoundException
     */
    public void verifyBaseDirectoryExists() throws FileNotFoundException
    {
        if (!esBaseDir.isDirectory())
        {
            throw new IllegalStateException(String.format(
                    "Base directory at %s does not exist", esBaseDir.getAbsolutePath()));
        }
    }
    
    /**
     * Verify that the Elasticsearch base directory does not exist on the disk.
     * 
     * @throws IllegalStateException
     */
    public void verifyBaseDirectoryNotExists() throws IllegalStateException
    {
        if (esBaseDir.exists())
        {
            throw new IllegalStateException(String.format(
                    "Base directory at %s exists", esBaseDir.getAbsolutePath()));
        }
    }

    /**
     * Verify that the ES instance in the Elasticsearch base directory is running
     * 
     * @param httpPort
     * @param clusterName
     * @throws IllegalStateException
     */
    public void verifyInstanceRunning(String clusterName, int httpPort) throws IllegalStateException
    {
        String path = esBaseDir.getAbsolutePath();
        if (Monitor.isProcessRunning(path) == false)
        {
            throw new IllegalStateException(String.format(
                    "The ES process in %s is not running", path));
        }
        
        if (Monitor.isInstanceRunning(clusterName, httpPort) == false)
        {
            throw new IllegalStateException(String.format(
                    "ES did not respond as expected to GET / request on port %d", httpPort));
        }
    }

    /**
     * Verify that the ES instance in the Elasticsearch base directory is not running
     * 
     * @param httpPort
     * @throws IllegalStateException
     */
    public void verifyInstanceNotRunning(String clusterName, int httpPort)
            throws IllegalStateException
    {
        String path = esBaseDir.getAbsolutePath();
        if (Monitor.isProcessRunning(path))
        {
            throw new IllegalStateException(String.format(
                    "The ES process in %s appears to be running", path));
        }
        
        if (Monitor.isInstanceRunning(clusterName, httpPort))
        {
            throw new IllegalStateException(String.format(
                    "ES responded with valid response to GET / request on port %d", httpPort));
        }
    }

}

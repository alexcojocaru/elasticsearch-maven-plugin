package com.github.alexcojocaru.mojo.elasticsearch.v2.step.resolveartifact;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;

/**
 * Resolver for the Elasticsearch artifact, playing the slave role
 * (ie. watching for the master resolver to finish downloading and installing).
 *
 * @author Alex Cojocaru
 *
 */
public class ElasticsearchArtifactResolverSlave
{
    private final ClusterConfiguration config;

    public ElasticsearchArtifactResolverSlave(ClusterConfiguration config)
    {
        this.config = config;
    }

    /**
     * Attempt to read the server port from the given lock file.
     * Throws a timeout exception if the port cannot be read.
     * @param lockFile The lock file to monitor
     * @return the server port (greater than 0), or -1 if the lock file has disappeared.
     * @throws ConditionTimeoutException if we time out waiting
     */
    public int readPort(File lockFile) throws ConditionTimeoutException
    {
        config.getLog().info("Waiting for the master process to start its server");

        MutableInt serverPort = new MutableInt(0);

        try
        {
            Awaitility
                    .await("server port in the lock file")
                    .atMost(15, TimeUnit.SECONDS)
                    .pollDelay(1, TimeUnit.SECONDS)
                    .pollInterval(1, TimeUnit.SECONDS)
                    .until(() -> {
                        if (lockFile.exists())
                        {
                            String content = null;
                            try
                            {
                                content = FileUtils.readFileToString(
                                        lockFile,
                                        Charset.defaultCharset());
                                if (StringUtils.isNotBlank(content))
                                {
                                    serverPort.setValue(Integer.parseInt(content.trim()));
                                }
                            }
                            catch(IOException e1)
                            {
                                config.getLog().debug(
                                        "Failed to read the content of the lock file;"
                                        + " this is unexpected, but lets not error out");
                            }
                            catch(NumberFormatException e2)
                            {
                                config.getLog().debug(
                                        "Failed to parse the file content '"
                                        + content + "' as integer");
                            }
                        }
                        else
                        {
                            config.getLog().debug(
                                    "The lock file disappeared;"
                                    + " stop trying to read the port");
                            serverPort.setValue(-1);
                        }
                        return serverPort.getValue() != 0;
                    });

            config.getLog().info("The master process is running its server on port " + serverPort);

            return serverPort.getValue();
        }
        catch (ConditionTimeoutException ex)
        {
            config.getLog().info(
                    "We have timed out waiting for the master process"
                    + " to write the server port in the lock file"
                    + " '" + lockFile.getAbsolutePath() + "';");
            throw ex;
        }
    }

    /**
     * Wait until the master resolver server has closed.
     * @param serverPort The server port to connect to
     * @throws ConditionTimeoutException if we time out waiting
     */
    public void waitForMasterResolverServer(int serverPort) throws ConditionTimeoutException
    {
        config.getLog().info("Waiting for the master process to finish downloading and installing");

        try
        {
            Awaitility
                    .await(
                            "download of Elasticsearch bundle by another plugin")
                    .atMost(10, TimeUnit.MINUTES)
                    .pollDelay(1, TimeUnit.SECONDS)
                    .pollInterval(1, TimeUnit.SECONDS)
                    .until(() -> {
                        Socket socket = null;
                        try
                        {
                            socket = new Socket(InetAddress.getLoopbackAddress(), serverPort);

                            config.getLog().debug(
                                    "Successfully connected to the other process;"
                                    + " this means the download is in process,"
                                    + " lets wait some more");

                            return false;
                        }
                        catch (IOException e1)
                        {
                            // cannot connect; assume the server has finished
                            config.getLog().debug(
                                    "Cannot connect to the other process;"
                                    + " assume the download has finished");

                            return true;
                        }
                        finally
                        {
                            if (socket != null)
                            {
                                try
                                {
                                    socket.close();
                                }
                                catch (Exception e1)
                                {
                                    // not much to do here
                                }
                            }
                        }
                    });

            config.getLog().info("The master process has finished downloading and installing");
        }
        catch (ConditionTimeoutException ex)
        {
            config.getLog().error(
                    "We have timed out waiting for the master process"
                    + " to download and install the artifact;"
                    + " its server is still running on port " + serverPort
                    + "; if it is stuck, please cancel it and restart this process");
            throw ex;
        }
    }

    public void waitForLockFileCleanup(File lockFile) throws ConditionTimeoutException
    {
        config.getLog().info("Waiting for the master process to clean up the lock file");

        try
        {
            Awaitility
                    .await("cleanup of the lock file by another plugin execution")
                    .atMost(15, TimeUnit.SECONDS)
                    .pollDelay(1, TimeUnit.SECONDS)
                    .pollInterval(1, TimeUnit.SECONDS)
                    .until(() -> {
                        boolean exists = lockFile.exists();
                        config.getLog().debug(
                                "Waiting for the lock file clean up; lock file exist = " + exists);
                        return exists == false;
                    });

            config.getLog().info("The master process has finished cleaning up the lock file");
        }
        catch (ConditionTimeoutException ex)
        {
            config.getLog().info(
                    "We have timed out waiting for the master process"
                    + " to clean up the lock file '" + lockFile.getAbsolutePath() + "'");
        }
    }

}

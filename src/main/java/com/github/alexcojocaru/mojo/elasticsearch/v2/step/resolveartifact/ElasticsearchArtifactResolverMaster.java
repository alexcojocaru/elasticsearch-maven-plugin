package com.github.alexcojocaru.mojo.elasticsearch.v2.step.resolveartifact;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchArtifact;
import com.github.alexcojocaru.mojo.elasticsearch.v2.configuration.ArtifactException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.FilesystemUtil;

/**
 * Resolver for the Elasticsearch artifact, playing the master role
 * (ie. running the download and installation of the artifact).
 *
 * @author Alex Cojocaru
 *
 */
public class ElasticsearchArtifactResolverMaster
{
    private final String ELASTICSEARCH_FILE_PARAM ="/%s";

    private final String ELASTICSEARCH_DOWNLOAD_URL =
            "https://artifacts.elastic.co/downloads/elasticsearch/%s";


    private final ClusterConfiguration config;

    private final ElasticsearchArtifact artifactReference;


    public ElasticsearchArtifactResolverMaster(
            ClusterConfiguration config,
            ElasticsearchArtifact artifactReference)
    {
        this.config = config;
        this.artifactReference = artifactReference;
    }

    /**
     * Run the "master" side of the artifact resolution:
     * start a server and write its port to the lock file,
     * then download and install the artifact.
     * The server is always closed at the end, even on exception.
     * @param lockFile The lock file to maintain
     * @throws ArtifactException when an artifact exception occurs
     * @throws IOException when an IO exception occurs
     */
    public void resolve(File lockFile) throws ArtifactException, IOException
    {
        // open a non-blocking server which will stay alive
        // until the artifact download is complete
        try (AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel
                    .open()
                    // bind to localhost and let it find a random open port
                    .bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0)))
        {
            InetSocketAddress address = (InetSocketAddress)listener.getLocalAddress();
            config.getLog().debug("Using port " + address.getPort() + " for the local server");

            // write the port to the lock file
            FileUtils.writeStringToFile(
                    lockFile,
                    String.valueOf(address.getPort()),
                    Charset.defaultCharset());
            config.getLog().debug("Wrote port " + address.getPort() + " to the lock file");

            listener.accept(null, createServerHandler(listener));
            config.getLog().debug("Started the server on port " + address.getPort());

            File tempFile = downloadArtifact();

            config.getArtifactInstaller().installArtifact(artifactReference, tempFile);
        }
    }

    private CompletionHandler<AsynchronousSocketChannel, Void> createServerHandler(
            AsynchronousServerSocketChannel listener)
    {
        return new CompletionHandler<AsynchronousSocketChannel, Void>()
                {
                    @Override
                    public void completed(AsynchronousSocketChannel client, Void attachment)
                    {
                        config.getLog().debug("New connection created");

                        // keep listening
                        if (listener.isOpen())
                        {
                            listener.accept(null, this);
                        }

                        if ((client != null) && (client.isOpen()))
                        {
                            try
                            {
                                client.close();
                            }
                            catch (IOException e)
                            {
                                config.getLog().warn("Failed to close the socket channel", e);
                            }
                            config.getLog().debug("New connection closed");
                        }
                        else
                        {
                            config.getLog().debug("New connection is null or already closed");
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment)
                    {
                        // not much to do; ignore it
                    }
                };
    }

    /**
     * Download the artifact from the download repository.
     * @return the downloaded file
     * @throws IOException when an IO exception occurs
     */
    private File downloadArtifact() throws IOException
    {
        config.getLog().debug("Downloading the ES artifact");

        String filename = artifactReference.buildBundleFilename();

        File tempFile = new File(FilesystemUtil.getTempDirectory(), filename);
        tempFile.deleteOnExit();
        FileUtils.deleteQuietly(tempFile);

        URL downloadUrl = new URL(
                StringUtils.isBlank(config.getDownloadUrl())
                        ? String.format(ELASTICSEARCH_DOWNLOAD_URL, filename)
                        : config.getDownloadUrl().endsWith(ELASTICSEARCH_FILE_PARAM)
                            ? String.format(config.getDownloadUrl(), filename)
                            : config.getDownloadUrl());

        config.getLog().info("Downloading " + downloadUrl + " to " + tempFile);
        URLConnection connection = downloadUrl.openConnection();
        Optional.ofNullable(config.getDownloadUrlUsername()).ifPresent(username -> {
            String basicAuthenticationEncoded = Base64.getEncoder().encodeToString((config.getDownloadUrlUsername() + ":" + config.getDownloadUrlPassword()).getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", "Basic " + basicAuthenticationEncoded);
        });
        try (OutputStream out = FileUtils.openOutputStream(tempFile)) {
            IOUtils.copyLarge(connection.getInputStream(), out);
        }

        return tempFile;
    }

}

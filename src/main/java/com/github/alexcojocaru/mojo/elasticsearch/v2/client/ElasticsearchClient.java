
package com.github.alexcojocaru.mojo.elasticsearch.v2.client;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.Validate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.maven.plugin.logging.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;

/**
 * A REST client for Elasticsearch.
 *
 * @author Alex Cojocaru
 */
public class ElasticsearchClient implements Closeable
{
    private final ObjectMapper mapper;
    private final HttpClient httpClient;
    private final PoolingHttpClientConnectionManager connectionManager;

    private final Log log;
    private final String hostname;
    private final int port;


    private ElasticsearchClient(
            Log log,
            ObjectMapper mapper,
            HttpClient httpClient,
            PoolingHttpClientConnectionManager connectionManager,
            String hostname,
            int port)
    {
        this.log = log;
        this.mapper = mapper;
        this.httpClient = httpClient;
        this.connectionManager = connectionManager;
        
        this.hostname = hostname;
        this.port = port;
    }


    @Override
    public void close()
    {
        connectionManager.close();
    }


    public <T> T get(String path, Class<T> clazz) throws ElasticsearchClientException
    {
        String uri = String.format("http://%s:%d%s", hostname, port, path);
        log.debug(String.format("Sending GET request to %s", uri));

        HttpGet request = new HttpGet(uri);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        String content = executeRequest(request);
        T result = deserialize(content, clazz);

        return result;
    }

    public <T> T get(String path, String entity, Class<T> clazz) throws ElasticsearchClientException
    {
        String uri = String.format("http://%s:%d%s", hostname, port, path);
        log.debug(String.format("Sending GET request to %s with entity '%s'", uri, entity));

        HttpGetWithEntity request = new HttpGetWithEntity(uri);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        request.setEntity(new StringEntity(entity, ContentType.APPLICATION_JSON));

        String content = executeRequest(request);
        T result = deserialize(content, clazz);

        return result;
    }

    public void put(String path, String entity) throws ElasticsearchClientException
    {
        String uri = String.format("http://%s:%d%s", hostname, port, path);
        log.info(String.format("Sending PUT request to %s with entity '%s'", uri, entity));

        HttpPut request = new HttpPut(uri);
        request.setEntity(new StringEntity(entity, ContentType.APPLICATION_JSON));
        executeRequest(request);
    }

    public <T> T post(String path, String entity, Class<T> clazz)
            throws ElasticsearchClientException
    {
        String uri = String.format("http://%s:%d%s", hostname, port, path);
        log.debug(String.format("Sending POST request to %s with entity '%s'", uri, entity));

        HttpPost request = new HttpPost(uri);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        request.setEntity(new StringEntity(entity, ContentType.APPLICATION_JSON));

        String content = executeRequest(request);
        T result = deserialize(content, clazz);

        return result;
    }

    public void delete(String path) throws ElasticsearchClientException
    {
        String uri = String.format("http://%s:%d%s", hostname, port, path);
        log.debug(String.format("Sending DELETE request to %s", uri));

        HttpDelete request = new HttpDelete(uri);
        executeRequest(request);
    }

    protected String readContent(HttpEntity entity)
            throws UnsupportedOperationException, IOException
    {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent())))
        {
            StringBuilder content = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null)
            {
                content.append(line);
            }

            return content.toString();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(String content, Class<T> clazz) throws ElasticsearchClientException
    {
        if (clazz == String.class)
        {
            return (T)content;
        }

        Validate.notNull(mapper, "Has the ElasticsearchClient been initialized?");
        
        try
        {
            return mapper.readValue(content, clazz);
        }
        catch (IOException ex)
        {
            throw new ElasticsearchClientException(String.format(
                    "Cannot deserialize the content '%s' to class %s", content, clazz));
        }
    }

    protected String executeRequest(HttpRequestBase request) throws ElasticsearchClientException
    {
        Validate.notNull(httpClient, "Has the ElasticsearchClient been initialized?");
        
        try
        {
            HttpResponse response = httpClient.execute(request);

            int statusCode = response.getStatusLine().getStatusCode();
            String content = readContent(response.getEntity());
            log.debug(String.format(
                    "Response with status code %d and content: %s", statusCode, content));

            // some PUT requests return 200, some 201 :-O
            if (statusCode != 200 && statusCode != 201)
            {
                throw new ElasticsearchClientException(request.getMethod(), statusCode, content);
            }

            return content;
        }
        catch (IOException e)
        {
            throw new ElasticsearchClientException(e);
        }
        finally
        {
            request.releaseConnection();
        }
    }
    
    
    public static class Builder
    {
        private Log log;
        private String hostname;
        private int port;
        private int socketTimeout;
        
        public Builder withLog(Log log)
        {
            this.log = log;
            return this;
        }
        
        public Builder withHostname(String hostname)
        {
            this.hostname = hostname;
            return this;
        }
        
        public Builder withPort(int port)
        {
            this.port = port;
            return this;
        }
        
        public Builder withSocketTimeout(int socketTimeout)
        {
            this.socketTimeout = socketTimeout;
            return this;
        }
        
        public Builder withInstanceConfiguration(InstanceConfiguration config)
        {
            this.log = config.getClusterConfiguration().getLog();
            this.socketTimeout = config.getClusterConfiguration().getClientSocketTimeout();
            this.port = config.getHttpPort();
            return this;
        }
        
        public ElasticsearchClient build()
        {
            PoolingHttpClientConnectionManager connectionManager = buildHttpClientManager(
                    socketTimeout);

            return new ElasticsearchClient(
                    log,
                    buildObjectMapper(),
                    buildHttpClient(connectionManager),
                    connectionManager,
                    hostname,
                    port);
        }
        
        private ObjectMapper buildObjectMapper()
        {
            return new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
    
        private static PoolingHttpClientConnectionManager buildHttpClientManager(int socketTimeout)
        {
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(3);
            cm.setDefaultMaxPerRoute(2);

            cm.setValidateAfterInactivity(1);

            cm.setDefaultSocketConfig(SocketConfig.custom()
                    .setSoTimeout(socketTimeout)
                    .setSoLinger(0)
                    .setTcpNoDelay(true)
                    .build());

            Runtime.getRuntime().addShutdownHook(new Thread()
            {
                @Override
                public void run()
                {
                    cm.close();
                }
            });

            return cm;
        }

        private static HttpClient buildHttpClient(HttpClientConnectionManager connectionManager)
        {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(1500)
                    .setConnectionRequestTimeout(1500)
                    .build();

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(requestConfig)
                    // use the default retry handler:
                    // https://hc.apache.org/httpcomponents-client-ga/tutorial/html/fundamentals.html#d5e305
                    .build();

            return httpClient;
        }
    }
}


package com.github.alexcojocaru.mojo.elasticsearch.v2.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A REST client for Elasticsearch.
 * 
 * @author Alex Cojocaru
 */
public class ElasticsearchClient
{
    private final String hostname;
    private final int port;

    private final static ObjectMapper mapper = buildObjectMapper();
    private final static HttpClient httpClient = buildHttpClient();

    private static ObjectMapper buildObjectMapper()
    {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);
    }

    private static HttpClientConnectionManager buildHttpClientManager()
    {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(3);
        cm.setDefaultMaxPerRoute(2);

        cm.setValidateAfterInactivity(1);

        cm.setDefaultSocketConfig(SocketConfig.custom()
                .setSoTimeout(1000)
                .setSoLinger(0)
                .setTcpNoDelay(true)
                .build());

        return cm;
    }

    private static HttpClient buildHttpClient()
    {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(1000)
                .setConnectionRequestTimeout(1000)
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(buildHttpClientManager())
                .setDefaultRequestConfig(requestConfig)
                // use the default retry handler:
                // https://hc.apache.org/httpcomponents-client-ga/tutorial/html/fundamentals.html#d5e305
                .build();

        return httpClient;
    }

    public ElasticsearchClient(String hostname, int port)
    {
        this.hostname = hostname;
        this.port = port;
    }

    public <T> T get(String path, Class<T> clazz) throws ElasticsearchClientException
    {
        String uri = String.format("http://%s:%d%s", hostname, port, path);

        try
        {
            // Create new getRequest with below mentioned URL
            HttpGet getRequest = new HttpGet(uri);

            // Add additional header to getRequest which accepts application/json data
            getRequest.addHeader("accept", "application/json");

            // Execute your request and catch response
            HttpResponse response = httpClient.execute(getRequest);

            // Check for HTTP response code: 200 = success
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200)
            {
                throw new RuntimeException("Failed : HTTP error code : " + statusCode);
            }

            String content = readContent(response.getEntity());
            T result = deserialize(content, clazz);

            return result;
        }
        catch (IOException e)
        {
            throw new ElasticsearchClientException(e);
        }
    }

    protected String readContent(HttpEntity entity)
            throws UnsupportedOperationException, IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
        StringBuilder content = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null)
        {
            content.append(line);
        }

        return content.toString();
    }

    protected <T> T deserialize(String content, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException
    {
        return mapper.readValue(content, clazz);
    }
}

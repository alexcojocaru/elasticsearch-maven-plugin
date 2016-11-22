package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;

import com.github.alexcojocaru.mojo.elasticsearch.v2.NetUtil.ElasticsearchPort;


/**
 * Used by the groovy setup scripts executed at the beginning of each integration test.
 * 
 * @author alexcojocaru
 */
public class ItSetup
{
    /**
     * The base direction of the test execution (ie. the project directory)
     */
    private final File baseDir;
    
    /**
     * @param executionBaseDir
     */
    public ItSetup(File baseDir)
    {
        this.baseDir = baseDir;
    }

    /**
     * Generate a map of properties to be passed to the plugin
     * 
     * @param count the number of ES instances
     * @throws IOException
     */
    public Map<String, String> generateProperties(int count) throws IOException
    {
        String clusterName = RandomStringUtils.randomAlphanumeric(8);
        Map<ElasticsearchPort, Integer> esPorts = NetUtil.findOpenPortsForElasticsearch(count);

        Map<String, String> props = new LinkedHashMap<>();
        props.put("es.instanceCount", String.valueOf(count));
        props.put("es.clusterName", clusterName);
        props.put("es.httpPort", esPorts.get(ElasticsearchPort.HTTP).toString());
        props.put("es.transportPort", esPorts.get(ElasticsearchPort.TRANSPORT).toString());

        return props;
    }

    /**
     * Write the given map to a properties file.
     */
    public void saveProperties(String filename, Map<String, String> props)
            throws IOException
    {
        Properties javaProps = new Properties();
        props.forEach((name, value) ->
        {
            javaProps.put(name, value);
        });

        File file = new File(baseDir, filename);
        javaProps.store(new FileOutputStream(file), null);
    }
}

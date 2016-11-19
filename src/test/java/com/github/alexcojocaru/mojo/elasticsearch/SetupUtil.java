package com.github.alexcojocaru.mojo.elasticsearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;

import com.github.alexcojocaru.mojo.elasticsearch.NetUtil.ElasticsearchPort;

/**
 * @author alexcojocaru
 */
public class SetupUtil
{
    /**
     * Generate a map of properties to be passed to the plugin
     * 
     * @throws IOException
     */
    public static Map<String, String> generateProperties() throws IOException
    {
        String clusterName = RandomStringUtils.randomAlphanumeric(8);
        Map<ElasticsearchPort, Integer> esPorts = NetUtil.findOpenPortsForElasticsearch();

        Map<String, String> props = new LinkedHashMap<>();
        props.put("clusterName", clusterName);
        props.put("httpPort", esPorts.get(ElasticsearchPort.HTTP).toString());
        props.put("transportPort", esPorts.get(ElasticsearchPort.TRANSPORT).toString());

        return props;
    }

    /**
     * Write the given map to a properties file.
     */
    public static void saveProperties(File dir, String filename, Map<String, String> props)
            throws IOException
    {
        Properties javaProps = new Properties();
        props.forEach((name, value) ->
        {
            javaProps.put(name, value);
        });

        File file = new File(dir, filename);
        javaProps.store(new FileOutputStream(file), null);
    }
}

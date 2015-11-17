package com.github.alexcojocaru.mojo.elasticsearch;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author alexcojocaru
 *
 */
public class NetUtil
{
    public enum ElasticsearchPort
    {
        HTTP,
        TCP
    };
    
    /**
     * Find two random open ports.
     * @return a map with values for the http.port and tcp.port key
     * @throws IOException
     */
    public static Map<ElasticsearchPort, Integer> findOpenPortsForElasticsearch()
            throws IOException
    {
        Map<ElasticsearchPort, Integer> ports = new HashMap<>();
        
        ServerSocket socket1 = null;
        ServerSocket socket2 = null;
        
        try
        {
            socket1 = new ServerSocket(0);
            socket2 = new ServerSocket(0);
            
            ports.put(ElasticsearchPort.HTTP, socket1.getLocalPort());
            ports.put(ElasticsearchPort.TCP, socket2.getLocalPort());
        }
        finally
        {
            if (socket1 != null)
            {
                try { socket1.close(); }
                catch (IOException ignored) { }
            }
            if (socket2 != null)
            {
                try { socket2.close(); }
                catch (IOException ignored) { }
            }
        }

        return ports;
    }
}

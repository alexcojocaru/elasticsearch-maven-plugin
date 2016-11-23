package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        TRANSPORT
    };
    
    /**
     * Find a pair of random open ports.
     * 
     * If the given instance count is greater than 1, check that the ports subsequent to the
     * initial random open ports are also open.
     * 
     * Example:
     * instanceCount is 3;
     * find a random open port => 1500;
     * check that 1501 and 1502 are open;
     * find a random open port => 1510;
     * check that 1511 and 1512 are open;
     * return (1500, 1510) 
     * 
     * @param instanceCount
     * @return a map with values for the http.port and tcp.port key
     * @throws IOException
     */
    public static Map<ElasticsearchPort, Integer> findOpenPortsForElasticsearch(int instanceCount)
            throws IOException
    {
        Map<ElasticsearchPort, Integer> ports = new HashMap<>();
        
        List<ServerSocket> httpPortSockets = new ArrayList<>();
        List<ServerSocket> transportPortSockets = new ArrayList<>();
        try
        {
            openSockets(instanceCount, httpPortSockets);
            openSockets(instanceCount, transportPortSockets);
            
            ports.put(ElasticsearchPort.HTTP, httpPortSockets.get(0).getLocalPort());
            ports.put(ElasticsearchPort.TRANSPORT, transportPortSockets.get(0).getLocalPort());
        }
        finally
        {
            httpPortSockets.forEach(socket -> closeSocket(socket));
            transportPortSockets.forEach(socket -> closeSocket(socket));
        }

        return ports;
    }
    
    /**
     * Open a socket on a random port and then a series of subsequent sockets,
     * and add them all to the given socket list.
     * @param count
     * @param socketList
     */
    private static void openSockets(int count, List<ServerSocket> socketList) throws IOException
    {
        // find an open port for the first instance
        ServerSocket initialSocket = new ServerSocket(0);
        socketList.add(initialSocket);

        // verify that the subsequent ports (for the remaining instances) are open
        for (int i = 1; i < count; i++)
        {
            ServerSocket subsequentSocket = new ServerSocket(initialSocket.getLocalPort() + i);
            socketList.add(subsequentSocket);
        }
    }
    
    private static void closeSocket(ServerSocket socket)
    {
        if (socket != null)
        {
            try { socket.close(); }
            catch (IOException ignored) { }
        }
    }
}

package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

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
    }

    private static final Random RANDOM = new Random(System.currentTimeMillis());
    
    /**
     * Find a pair of random open ports.
     * <p></p>
     * If the given instance count is greater than 1, check that the ports subsequent to the
     * initial random open ports are also open.
     * <p></p>
     * Example:
     * instanceCount is 3;
     * find a random open port => 1500;
     * check that 1501 and 1502 are open;
     * find a random open port => 1510;
     * check that 1511 and 1512 are open;
     * return (1500, 1510) 
     * 
     * @param instanceCount how many ES instances to find open ports for
     * @return a map with values for the http.port and tcp.port key
     * @throws IOException if open ports cannot be found
     */
    public static Map<ElasticsearchPort, Integer> findOpenPortsForElasticsearch(int instanceCount)
            throws IOException
    {
        // we need two sets of ports, one for HTTP, one for TRANSPORT,
        // hence the instanceCount*2 ports to find
        int port = findAvailablePorts(instanceCount * 2);

        Map<ElasticsearchPort, Integer> ports = new HashMap<>();
        ports.put(ElasticsearchPort.HTTP, port);
        ports.put(ElasticsearchPort.TRANSPORT, port + instanceCount);

        return ports;

//        List<ServerSocket> httpPortSockets = new ArrayList<>();
//        List<ServerSocket> transportPortSockets = new ArrayList<>();
//
//        try
//        {
//            openSockets(instanceCount, httpPortSockets);
//            openSockets(instanceCount, transportPortSockets);
//
//            ports.put(ElasticsearchPort.HTTP, httpPortSockets.get(0).getLocalPort());
//            ports.put(ElasticsearchPort.TRANSPORT, transportPortSockets.get(0).getLocalPort());
//        }
//        finally
//        {
//            httpPortSockets.stream().filter(Objects::nonNull).forEach(ServerSocket::close);
//            transportPortSockets.stream().filter(Objects::nonNull).forEach(ServerSocket::close);
//        }
    }

    private static int findAvailablePorts(int count) throws IOException
    {
        int basePort;
        int tryCount = 0;
        do
        {
            basePort = getRandomPort();
            tryCount++;
        }
        while (isPortsAvailable(basePort, count) == false && tryCount < 10);

        if (tryCount == 10)
        {
            throw new IOException("Cannot find ports to bind Elasticsearch to after 10 tries");
        }

        return basePort;
    }

    private static int getRandomPort()
    {
        int minPort = 1025;
        int maxPort = 65535;
        return minPort + RANDOM.nextInt(maxPort - minPort);
    }

    private static boolean isPortsAvailable(int basePort, int count)
    {
        return IntStream
                .range(basePort, basePort + count)
                .boxed()
                .allMatch(NetUtil::isPortAvailable); // stop at the first unavailable port
    }

    private static boolean isPortAvailable(int port)
    {
        try (Socket socket = new Socket())
        {
            socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), port), 20);
            return false;
        } catch (Throwable e) {
            return true;
        }
    }

    //    /**
//     * Open a socket on a random port and then a series of subsequent sockets,
//     * and add them all to the given socket list.
//     * @param count
//     * @param socketList
//     */
//    private static void openSockets(int count, List<ServerSocket> socketList) throws IOException
//    {
//        // find an open port for the first instance
//        ServerSocket initialSocket = new ServerSocket(0);
//        socketList.add(initialSocket);
//
//        // verify that the subsequent ports (for the remaining instances) are open
//        for (int i = 1; i < count; i++)
//        {
//            ServerSocket subsequentSocket = new ServerSocket(initialSocket.getLocalPort() + i);
//            socketList.add(subsequentSocket);
//        }
//    }
}

package server.tasks;

import common.net.agent.AbstractAgent;
import common.net.data.Entity;
import common.net.tcp.TCPListener;
import common.net.udp.UDPListener;

import java.io.IOException;
import java.security.SecureRandom;

public class Connect implements Runnable{
    AbstractAgent server;
    int portNumber;
    TCPListener tcpListener;
    UDPListener udpListener;

    public Connect(AbstractAgent server, int portNumber, TCPListener tcpListener, UDPListener udpListener) {
        this.server = server;
        this.portNumber = portNumber;
        this.tcpListener = tcpListener;
        this.udpListener = udpListener;
    }

    @Override
    public void run() {
        var secureRandom = new SecureRandom();
        long random = secureRandom.nextLong();
        try {
            var tcpConnection = tcpListener.listen(portNumber);
            var udpConnection = udpListener.listen(portNumber);
            var entity = new Entity(tcpConnection, udpConnection, random);
            server.syncID(entity);
            server.registerEntity(entity);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("New client connected on port : " + portNumber);
    }
}

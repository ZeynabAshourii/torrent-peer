package client.tasks;

import client.Client;
import common.net.agent.NetworkingPolicies;
import common.net.data.Entity;
import common.net.tcp.TCPConnection;
import common.net.udp.UDPConnection;
import lombok.Getter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Connect implements Runnable{

    String remoteName;
    int remotePort;
    Client self;
    NetworkingPolicies policies;
    @Getter
    AtomicBoolean idle = new AtomicBoolean(true);


    public Connect(String remoteName, int remotePort, Client self, NetworkingPolicies policies) {
        this.remoteName = remoteName;
        this.remotePort = remotePort;
        this.self = self;
        this.policies = policies;
    }

    @Override
    public void run() {
        TCPConnection tcpConnection;
        UDPConnection udpConnection;
        Entity server;
        try {
            tcpConnection = new TCPConnection();
            tcpConnection.connectTo(remoteName, remotePort, "localhost", 0);
            udpConnection = new UDPConnection();
            udpConnection.connectTo(remoteName, remotePort, "localhost", 0);
            server = new Entity(tcpConnection, udpConnection, 0);
            self.syncID(server);
        } catch (IOException e) {
            System.out.println("Failed to connect!");
            idle.set(true);
            return;
        }
        self.registerEntity(server);
        self.setServer(server);
        idle.set(true);
        System.out.println("Client connected to remote host : " + remoteName + ":" + remotePort);
    }
}

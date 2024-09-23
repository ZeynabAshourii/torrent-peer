package client;

import client.tasks.Connect;
import common.net.agent.AbstractAgent;
import common.net.data.Command;
import common.net.data.Entity;
import lombok.Setter;
import lombok.SneakyThrows;
import policies.NetRules;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class Client extends AbstractAgent {
    @Setter
    String remoteName = "localhost";
    @Setter
    int remotePort = 9001;
    Entity server;
    final AtomicReference<Connect> connectTaskReference = new AtomicReference<>();
    @Setter
    Runnable connectionTrigger;
    private final Object connectionLock = new Object();

    public Client() {
        super(new NetRules(), 1);
    }

    public void init() {this.pool = Executors.newSingleThreadExecutor();}

    public void connect() {
        synchronized (connectionLock) {
            connectTaskReference.compareAndSet(null, new Connect(remoteName, remotePort, this, policies));
            if (connectTaskReference.get().getIdle().compareAndSet(true, false) && !isConnected())
                pool.submit(connectTaskReference.get());
        }
    }

    @SneakyThrows
    public void disconnect() {
        synchronized (connectionLock) {
            if (isConnected())
                terminateConnection(server);
            server = null;
        }
    }

    public void syncID(Entity entity) throws IOException {
        var packet = entity.getTcp().fetch();
        entity.setId(packet.id);
    }

    @Override
    public void registerEntity(Entity entity) {
        super.registerEntity(entity);
        // new Ping(this, policies).start();
    }

    public Entity getServer() {
        return server;
    }

    public void setServer(Entity server) {
        this.server = server;
        pool.submit(connectionTrigger);
    }

    public synchronized boolean isConnected() {return server != null;}

    public Command read() {
        if (inbound.isEmpty())
            return null;
        else return inbound.remove().command;
    }
}

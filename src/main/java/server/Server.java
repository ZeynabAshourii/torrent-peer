package server;

import common.net.agent.AbstractAgent;
import common.net.data.Command;
import common.net.data.Entity;
import common.net.data.Packet;
import lombok.Setter;
import policies.NetRules;
import server.tasks.Connect;

import java.io.IOException;
import java.util.concurrent.Executors;

public class Server extends AbstractAgent {
    @Setter
    int portNumber = 9001;

    boolean started;

    public Server() {
        super(new NetRules(), 20);
    }

    public void init() {
        pool = Executors.newFixedThreadPool(capacity);
    }


    private void startListening() {
        System.out.println("Server started on port " + portNumber + " with capacity " + capacity);
        for (int i = 0; i < capacity; i++) {
            pool.submit(new Connect(this, portNumber, tcpListener, udpListener));
        }
        started = true;
        enableReceiving();
    }
    public void start() {
        if (!started)
            startListening();
        else
            throw new RuntimeException("Server already started!");
    }

    @Override
    public void syncID(Entity entity) throws IOException {
        var packet = new Packet(new Command(null), entity.getId());
        entity.getTcp().send(packet);
    }

    @Override
    public void registerEntity(Entity entity) {
        super.registerEntity(entity);
        // new Heartbeat(this, entity).start();
    }

    public boolean isAvailable(Entity entity) {
        return entities.contains(entity);
    }
}

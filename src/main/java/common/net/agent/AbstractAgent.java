package common.net.agent;

import common.net.data.Command;
import common.net.data.Entity;
import common.net.data.Packet;
import common.net.tcp.TCPListener;
import common.net.udp.UDPListener;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

public abstract class AbstractAgent {

    protected final List<Entity> entities = new CopyOnWriteArrayList<>();
    protected final Queue<Packet> inbound = new ConcurrentLinkedQueue<>();
    protected final Queue<Packet> outbound = new ConcurrentLinkedQueue<>();
    protected final Broadcast broadcast = new Broadcast(this);
    protected final Receive receive;
    protected ExecutorService pool;
    protected final TCPListener tcpListener = new TCPListener();
    protected final UDPListener udpListener = new UDPListener();
    protected final NetworkingPolicies policies;
    protected final int capacity;

    public AbstractAgent(NetworkingPolicies policies, int capacity) {
        this.policies = policies;
        broadcast.setPolicies(policies);
        this.capacity = capacity;
        receive = new Receive(this);
        receive.setPolicies(policies);
        receive.pause();
        receive.start();
    }

    public void addToOutbound(Command command, Entity entity) {
        if (entities.contains(entity)) {
            // check policy stack
            var packet = policies.generatePacket(command, entity);
            if (packet != null)
                outbound.add(packet);
        }
    }

    public void enableReceiving() {
        receive.restart();
    }

    public void disableReceiving() {
        receive.pause();
    }

    public void addToInbound(Packet incoming) {
        inbound.add(incoming);
    }

    public void registerEntity(Entity entity) {
        entities.add(entity);
        receive.restart();
    }

    protected void send(Command command, Entity entity) {
        addToOutbound(command, entity);
        broadcast.cast();
    }

    public void send(Command command) {
        if (command.getEntity() == null)
            throw new RuntimeException("Command recipient not set !");
        send(command, command.getEntity());
    }

    public abstract void syncID(Entity entity) throws IOException;

    protected synchronized void terminateConnection(Entity entity) throws IOException {
        if (entities.contains(entity)) {
            entities.remove(entity);
            entity.getTcp().disconnect();
            entity.getUdp().disconnect();
            entity.setAlive(false);
            System.err.println("Terminated " + entity.getId());
        }
    }

    public void setRetrievalAction(Runnable task) {receive.setRetrievalAction(task);}

    public Optional<Packet> readPacket() {
        if (inbound.isEmpty())
            return Optional.empty();
        return Optional.of(inbound.remove());
    }
}

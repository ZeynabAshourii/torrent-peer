package common.net.agent;

import common.net.Connection;
import common.net.data.Entity;
import util.Semaphore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Receive extends Thread {
    private final ExecutorService executors;
    private final AbstractAgent agent;
    private boolean started;
    private final Semaphore semaphore = new Semaphore(0);
    private boolean paused = true;
    private boolean killed;
    private Runnable retrievalAction;

    public Receive(AbstractAgent agent) {
        this.agent = agent;
        executors = Executors.newFixedThreadPool(2 * agent.capacity);
    }

    public void setPolicies(NetworkingPolicies stack) {
        Fetch.setStack(stack);
        started = true;
    }

    public void run() {
        while (!killed) {
            if (paused)
                semaphore.forceLock();
            receive();
        }
    }

    private void pollEntities() {
        synchronized (semaphore) {
            for (var entity : agent.entities) {
                var tcp = entity.getTcp();
                if (tcp.isIdle()) {
                    tcp.setIdle(false);
                    executors.submit(new Fetch(tcp, entity, agent, this, retrievalAction));
                }
                var udp = entity.getUdp();
                if (udp.isIdle()) {
                    udp.setIdle(false);
                    executors.submit(new Fetch(udp, entity, agent, this, retrievalAction));
                }
            }
        }
        semaphore.forceLock();
    }

    private void receive() {
        if (started)
            pollEntities();
        else
            throw new RuntimeException("Receive policies not set!");
    }

    public void pause() {
        synchronized (semaphore) {
            paused = true;
        }
    }

    public void restart() {
        synchronized (semaphore) {
            paused = false;
            semaphore.forceRelease();
        }
    }

    public void kill() {
        synchronized (semaphore) {
            killed = true;
        }
    }

    public void setRetrievalAction(Runnable retrievalAction) {
        this.retrievalAction = retrievalAction;
    }

    private static class Fetch implements Runnable {
        Connection connection;
        Entity entity;
        AbstractAgent agent;
        static NetworkingPolicies stack;
        Receive thread;
        Runnable action;

        public Fetch(Connection connection, Entity entity, AbstractAgent agent, Receive thread, Runnable action) {
            this.connection = connection;
            this.entity = entity;
            this.agent = agent;
            this.thread = thread;
            this.action = action;
        }

        @Override
        public void run() {
            try {
                var packet = connection.fetch();
                stack.routePacket(packet, entity, agent);
                System.out.println(packet.command.headers);
                if (action != null)
                    action.run();
            } catch (Exception ex) {
                try {
                    agent.terminateConnection(entity);
                } catch (Exception ignored) {}
            }
            connection.setIdle(true);
            thread.restart();
        }

        public static void setStack(NetworkingPolicies networkingPolicies) {
            stack = networkingPolicies;
        }
    }
}

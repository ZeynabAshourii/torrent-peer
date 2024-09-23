package common.net.agent;

import common.net.data.Packet;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Broadcast {
    protected final ExecutorService executor = Executors.newSingleThreadExecutor();
    protected final AbstractAgent agent;
    protected boolean started;

    public void setPolicies(NetworkingPolicies stack) {
        Send.setPolicies(stack);
        started = true;
    }

    public Broadcast(AbstractAgent agent) {
        this.agent = agent;
    }

    public void cast() {
        if (started)
            executor.submit(new Send(agent.outbound.poll(), agent));
        else
            throw new RuntimeException("Broadcast policies not set!");
    }

    private static class Send implements Runnable {
        Packet out;
        AbstractAgent agent;
        static NetworkingPolicies policies;

        public Send(Packet out, AbstractAgent agent) {
            this.out = out;
            this.agent = agent;
        }

        @Override
        public void run() {
            if (out == null)
                return;

            try {
                policies.getConnectionType(out).getConnection(out.entity).send(out);
            } catch (IOException e) {
                e.printStackTrace();
                //throw new RuntimeException(e);
            }
        }

        public static void setPolicies(NetworkingPolicies policies) {
            Send.policies = policies;
        }
    }
}

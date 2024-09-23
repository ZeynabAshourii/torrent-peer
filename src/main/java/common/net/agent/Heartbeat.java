package common.net.agent;

import common.net.data.Entity;
import util.Routine;

import java.io.IOException;

public class Heartbeat extends Routine {
    public Heartbeat(AbstractAgent agent, Entity entity) {
        super(1, new Lookup(entity, agent, 20)); // todo : move these to policy stack
    }
    private static class Lookup implements Runnable {
        AbstractAgent agent;
        int tolerance;
        int missCount;
        Entity entity;

        public Lookup(Entity entity, AbstractAgent agent, int missCount) {
            this.agent = agent;
            this.tolerance = missCount;
            this.entity = entity;
        }

        @Override
        public void run() {
            if (!entity.getHeartbeats().isEmpty()) {
                missCount = 0;
                clearQueue();
                return;
            }
            missCount++;

            // terminate socket
            try {
                if (tolerance == missCount)
                    agent.terminateConnection(entity);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void clearQueue() {
            entity.getHeartbeats().clear();
        }
    }
}

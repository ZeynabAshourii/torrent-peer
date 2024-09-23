package policies;

import common.net.agent.AbstractAgent;
import common.net.agent.PacketValidator;

public class RetrievalBehaviour implements Runnable {
    AbstractAgent agent;
    PacketValidator validator;

    public RetrievalBehaviour(AbstractAgent agent, PacketValidator validator) {
        this.agent = agent;
        this.validator = validator;
    }

    @Override
    public void run() {
        var optional = agent.readPacket();
        optional.ifPresent(packet -> validator.validate(packet));
    }
}

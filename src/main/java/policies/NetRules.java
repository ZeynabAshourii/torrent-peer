package policies;

import common.net.agent.AbstractAgent;
import common.net.agent.NetworkingPolicies;
import common.net.data.Command;
import common.net.data.ConnectionType;
import common.net.data.Entity;
import common.net.data.Packet;

public class NetRules extends NetworkingPolicies {
    @Override
    public Packet generatePacket(Command command, Entity entity) {
        // raw packet
        var packet = new Packet(null, 0);

        if (command.headers.containsKey("heartbeat")) {
            // todo : get client id from server in handshake
            packet.id = entity.getId();
            command.headers.remove("heartbeat"); // command.headers = null ??
        }

        packet.setEntity(entity);
        packet.command = command;

        return packet;
    }

    @Override
    public ConnectionType getConnectionType(Packet packet) {
        if (packet.id == 0) {
            if (packet.command.headers.containsKey("connection-type")) {
                Object v = packet.command.getHeader("connection-type");
                packet.command.headers.remove("connection-type");
                if ("udp".equals(v))
                    return ConnectionType.UDP;
                else if ("tcp".equals(v))
                    return ConnectionType.TCP;
                else
                    throw new RuntimeException("Invalid property : connection-type");
            }
            else throw new RuntimeException("Command missing property : connection-type");
        }
        else
            return ConnectionType.UDP;
    }

    @Override
    public void routePacket(Packet in, Entity entity, AbstractAgent agent) {
        if (in.id != 0) {
            entity.getHeartbeats().add(in);
        }
        else {
            in.setEntity(entity);
            agent.addToInbound(in);
        }
    }

    @Override
    public Packet generateBeat(Entity entity) {
        var packet = new Packet(new Command(entity).addHeader("heartbeat", "heartbeat"), entity.getId());
        packet.setEntity(entity);
        return packet;
    }
}

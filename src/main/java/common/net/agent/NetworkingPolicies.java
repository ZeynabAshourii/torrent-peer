package common.net.agent;

import common.net.data.Command;
import common.net.data.ConnectionType;
import common.net.data.Entity;
import common.net.data.Packet;

public abstract class NetworkingPolicies {

    public abstract Packet generatePacket(Command command, Entity entity);

    public abstract ConnectionType getConnectionType(Packet packet);

    public abstract void routePacket(Packet in, Entity entity, AbstractAgent agent);

    public abstract Packet generateBeat(Entity entity);
}

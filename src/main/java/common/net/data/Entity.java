package common.net.data;

import common.net.Connection;
import lombok.Getter;
import lombok.Setter;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Entity {
    @Getter
    private final Connection tcp;
    @Getter
    private final Connection udp;
    @Getter
    @Setter
    private long id;
    @Getter
    private final Queue<Packet> heartbeats = new ConcurrentLinkedQueue<>();
    @Getter
    @Setter
    private boolean alive;

    public Entity(Connection tcp, Connection udp, long id) {
        this.tcp = tcp;
        this.udp = udp;
        this.id = id;
        this.alive = true;
    }
}

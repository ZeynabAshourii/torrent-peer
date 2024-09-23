package common.net;

import common.net.data.Packet;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.SocketException;
/* every connection object has to be used by a single thread only and cannot be shared by multiple threads */
public abstract class Connection {
    protected boolean connected;
    public abstract void connectTo(String remoteAddress, int remotePort, String localAddress, int localPort) throws IOException;
    public abstract void disconnect() throws IOException;
    public abstract void send(Packet data) throws IOException;
    public abstract Packet fetch() throws IOException;
    public abstract void setTimeout(int millis) throws SocketException;
    @Getter
    @Setter
    private boolean idle = true;
}

package common.net.udp;

import common.net.Connection;
import common.net.Listener;
import common.net.data.Command;
import common.net.data.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static common.net.data.Packet.generateUniversalPacket;

public class UDPListener extends Listener {
    protected DatagramSocket socket;
    @Override
    public Connection listen(int portNumber) throws IOException {
        synchronized (this) {
            if (!started)
                init(portNumber);
        }
        return new ConnectionImpl(socket);
    }

    @Override
    protected void init(int portNumber) throws IOException {
        socket = new DatagramSocket(portNumber);
        started = true;
    }

    @Override
    public void close() throws IOException {}

    private static class ConnectionImpl extends UDPConnection {
        InetAddress remoteAddress;
        int remotePort;
        public ConnectionImpl(DatagramSocket socket) throws IOException {
            this.socket = socket;
            handShake();
        }

        @Override
        public synchronized void send(Packet data) throws IOException {
            if (!connected)
                throw new RuntimeException("Address not set!");
            var bytes = Packet.toBytes(data);
            byte[] length = ByteUtils.toBytes(bytes.length);
            System.arraycopy(length, 0, writeBuffer, 0, 2);
            System.arraycopy(bytes, 0, writeBuffer, 2, bytes.length);
            var datagramPacket = new DatagramPacket(writeBuffer, bytes.length + 2, remoteAddress, remotePort);
            socket.send(datagramPacket);
        }

        protected void init(DatagramPacket firstPacket) {
            remoteAddress = firstPacket.getAddress();
            remotePort = firstPacket.getPort();
            connected = true;
        }

        @Override
        protected void handShake() throws IOException {
            var datagramPacket = readPacket();
            var universalPacket = generateUniversalPacket(datagramPacket);
            while (universalPacket.id != -1 || !universalPacket.command.headers.containsKey("syn")) {
                datagramPacket = readPacket();
                universalPacket = generateUniversalPacket(datagramPacket);
            }
            init(datagramPacket);
            for (int i = 0; i < 10; i++) {
                send(new Packet(new Command(null).addHeader("ack", "ack"), -1));
            }
        }
    }
}

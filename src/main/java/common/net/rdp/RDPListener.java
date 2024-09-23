package common.net.rdp;

import common.net.Connection;
import common.net.data.Command;
import common.net.data.Packet;
import common.net.udp.ByteUtils;
import common.net.udp.UDPListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RDPListener extends UDPListener {
    @Override
    public Connection listen(int portNumber) throws IOException {
        synchronized (this) {
            if (!started)
                init(portNumber);
        }
        return new ConnectionImpl(socket);
    }

    private static class ConnectionImpl extends RDPConnection {
        InetAddress remoteAddress;
        int remotePort;
        public ConnectionImpl(DatagramSocket socket) throws IOException {
            this.socket = socket;
            handShake();
            start();
        }

        private synchronized void send(Packet data, long current, boolean queued) throws IOException {
            if (!connected)
                throw new RuntimeException("Address not set!");
            if (queued) {
                last_seq_wrt = current;
                data.id = current;
                out_arr[(int) current] = data;
            }
            var bytes = Packet.toBytes(data);
            byte[] length = ByteUtils.toBytes(bytes.length);
            System.arraycopy(length, 0, writeBuffer, 0, 2);
            System.arraycopy(bytes, 0, writeBuffer, 2, bytes.length);
            var datagramPacket = new DatagramPacket(writeBuffer, bytes.length + 2, remoteAddress, remotePort);
            socket.send(datagramPacket);
        }

        protected void flush(int index) throws IOException {
            for (int i  = index; i < 2000; i++) {
                if (out_arr[i] != null) {
                    send(out_arr[i], 0, false);
                    try {
                        Thread.sleep(5);
                    } catch (Exception ignored) {}
                }
            }
        }

        public void send(Packet data) throws IOException {
            long current = (last_seq_wrt + 1) % 2000;
            ensure(data, current);
            send(data, current, true);
        }

        void send(byte[] pack) throws IOException {
            var packet = new DatagramPacket(pack, pack.length, remoteAddress, remotePort);
            socket.send(packet);
        }

        protected void init(DatagramPacket firstPacket) {
            remoteAddress = firstPacket.getAddress();
            remotePort = firstPacket.getPort();
            connected = true;
        }

        @Override
        protected void handShake() throws IOException {
            var datagramPacket = readPacket();
            var universalPacket = Packet.generateUniversalPacket(datagramPacket);
            while (universalPacket.id != -1 || !universalPacket.command.headers.containsKey("syn")) {
                datagramPacket = readPacket();
                universalPacket = Packet.generateUniversalPacket(datagramPacket);
            }
            init(datagramPacket);
            for (int i = 0; i < 10; i++)
                send(new Packet(new Command(null).addHeader("ack", "ack"), -1), 0, false);
        }
    }
}

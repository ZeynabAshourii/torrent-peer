package common.net.udp;

import common.net.Connection;
import common.net.data.Command;
import common.net.data.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import static common.net.data.Packet.generateUniversalPacket;

/* id -1 is reserved */
public class UDPConnection extends Connection {
    protected DatagramSocket socket;
    protected final byte[] writeBuffer = new byte[65000];
    protected final byte[] readBuffer = new byte[65000];

    @Override
    public void connectTo(String remoteAddress, int remotePort, String localAddress, int localPort) throws IOException {
        socket = new DatagramSocket();
        socket.connect(InetAddress.getByName(remoteAddress), remotePort);
        handShake();
        connected = true;
    }

    @Override
    public void disconnect() throws IOException {
        connected = false;
    }

    @Override
    public synchronized void send(Packet data) throws IOException {
        var bytes = Packet.toBytes(data);
        byte[] length = ByteUtils.toBytes(bytes.length);
        System.arraycopy(length, 0, writeBuffer, 0, 2);
        System.arraycopy(bytes, 0, writeBuffer, 2, bytes.length);
        var datagramPacket = new DatagramPacket(writeBuffer, bytes.length + 2);
        socket.send(datagramPacket);
    }

    public DatagramPacket readPacket() throws IOException {
        var input = new DatagramPacket(readBuffer, readBuffer.length);
        socket.receive(input);
        return input;
    }

    @Override
    public  Packet fetch() throws IOException {
        var packet = generateUniversalPacket(readPacket());
        if (connected)
            while (packet.id == -1)
                packet = generateUniversalPacket(readPacket());
        return packet;
    }

    protected void handShake() throws IOException {
        var timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    send(new Packet(new Command(null).addHeader("syn", "syn"), -1));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        },0, 500);
        var universalPacket = fetch();
        while (universalPacket.id != -1 || !universalPacket.command.headers.containsKey("ack"))
            universalPacket = fetch();
        timer.cancel();
    }




    public void setTimeout(int millis) throws SocketException {
        socket.setSoTimeout(millis);
    }
}

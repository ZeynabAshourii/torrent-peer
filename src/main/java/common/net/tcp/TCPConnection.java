package common.net.tcp;

import common.net.Connection;
import common.net.data.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class TCPConnection extends Connection {
    protected Socket socket;
    protected ObjectInputStream inputStream;
    protected ObjectOutputStream outputStream;

    public void connectTo(String address, int portNumber, String localAddress, int localPort) throws IOException {
        if (connected) {
            throw new RuntimeException("Socket already connected!");
        }
        socket = new Socket(InetAddress.getByName(address), portNumber, InetAddress.getByName(localAddress), localPort);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        connected = true;
    }

    public void disconnect() throws IOException {
        if (connected) {
            socket.close();
            connected = false;
        } else
            throw new RuntimeException("Socket already closed!");
    }

    public void send (Packet data) throws IOException {
        if (connected) {
            outputStream.writeObject(data);
            outputStream.reset();
        }
        else
            throw new RuntimeException("Socket connection is closed!");
    }

    @Override
    public Packet fetch() throws IOException {
        try {
            return Packet.toPacket(inputStream.readObject());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTimeout(int millis) throws SocketException {
        socket.setSoTimeout(millis);
    }
}

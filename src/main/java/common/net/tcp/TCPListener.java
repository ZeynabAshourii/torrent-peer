package common.net.tcp;

import common.net.Connection;
import common.net.Listener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPListener extends Listener {
    private ServerSocket socket;

    public Connection listen(int portNumber) throws IOException {
        synchronized (this) {
            if (!started)
                init(portNumber);
        }
        return new ConnectionImpl(socket.accept());
    }

    protected void init(int portNumber) throws IOException {
        socket = new ServerSocket(portNumber);
        started = true;
    }

    public void close() throws IOException {
        socket.close();
    }

    private static class ConnectionImpl extends TCPConnection {
        public ConnectionImpl(Socket socket) throws IOException {
            this.socket = socket;
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            connected = true;
        }
    }
}

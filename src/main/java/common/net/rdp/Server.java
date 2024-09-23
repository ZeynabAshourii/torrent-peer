package common.net.rdp;

import common.net.Connection;
import common.net.data.Command;
import common.net.data.Packet;

import java.io.IOException;

public class Server extends Thread{
    public void run () {
        var listener = new RDPListener();
        Connection connection;
        try {
            connection = listener.listen(9000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final int[] num = new int[]{0};
        new Thread(() -> {
            while (true) {
                try {
                    connection.send(new Packet(new Command(null).addHeader("header", num), 0));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                num[0]++;
            }
        }).start();
    }
}

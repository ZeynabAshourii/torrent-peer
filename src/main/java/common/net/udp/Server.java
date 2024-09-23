package common.net.udp;

import common.net.Connection;

import java.io.IOException;

public class Server extends Thread{
    public void run () {
        var listener = new UDPListener();
        Connection connection;
        try {
            connection = listener.listen(9000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*while (true) {
            Packet packet;
            try {
                packet = connection.fetch();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            var k = packet.command.headers.keySet().toArray()[0];
            var v = (double) packet.command.getHeader(k);
            var newPacket = new Packet(new Command(null).addHeader(k, v + 1), 10);
            try {
                connection.send(newPacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }*/
        try {
            while (true)
                System.out.println(connection.fetch().command.getHeader("shit"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

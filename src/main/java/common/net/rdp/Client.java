package common.net.rdp;


import java.io.IOException;


public class Client extends Thread{
    public void run() {
        try {
            var connection = new RDPConnection();
            connection.connectTo("localhost", 9000, "localhost", 8000);
            while (true) {
                try {
                    System.out.println(connection.fetch().command.headers);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

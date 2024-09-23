package common.net.tcp;


public class Client extends Thread{
    public void run() {
        try {
            var connection = new TCPConnection();
            connection.connectTo("localhost", 9000, "localhost", 8000);
            /*System.out.println(connection.fetch().command.headers);*/
            while (true)
                System.out.println(connection.fetch().command.getHeader("shit"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

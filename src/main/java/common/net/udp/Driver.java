package common.net.udp;

public class Driver {
    public static void main(String[] args) {
        new Server().start();
        new Client().start();
    }
}

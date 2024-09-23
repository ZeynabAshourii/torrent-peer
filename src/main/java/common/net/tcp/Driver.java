package common.net.tcp;

import java.io.IOException;

public class Driver {
    public static void main(String[] args) throws IOException {
        new Server().start();
        new Client().start();
    }
}

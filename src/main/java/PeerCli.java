import control.App;
import lombok.SneakyThrows;
import util.Config;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class PeerCli {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        var app = new App();
        System.out.println("peer created");
        while (app.alive) {
            String order = scanner.nextLine();
            if (order.length() >= 5 && (order.substring(0, 5).equals("share"))) {
                int firstAdd = 0;
                int secondAdd = 0;
                for (int i = 6; i < order.length() - 10; i++) {
                    if (order.substring(i, i + 10).equals("127.0.0.1:")) {
                        firstAdd = i;
                        break;
                    }
                }
                String fileName = order.substring(6, firstAdd - 1);
                for (int i = firstAdd + 10; i < order.length() - 10; i++) {
                    if (order.substring(i, i + 10).equals("127.0.0.1:")) {
                        secondAdd = i;
                        break;
                    }
                }
                int trackerPort = Integer.parseInt(order.substring(firstAdd + 10, secondAdd - 1));
                int listenPort = Integer.parseInt(order.substring(secondAdd + 10));
                app.shareFile(fileName, "localhost", trackerPort, listenPort);

            } else if (order.length() >= 3 && (order.substring(0, 3).equals("get"))) {
                int firstAdd = 0;
                int secondAdd = 0;
                for (int i = 4; i < order.length() - 10; i++) {
                    if (order.substring(i, i + 10).equals("127.0.0.1:")) {
                        firstAdd = i;
                        break;
                    }
                }
                String fileName = order.substring(4, firstAdd - 1);
                for (int i = firstAdd + 10; i < order.length() - 10; i++) {
                    if (order.substring(i, i + 10).equals("127.0.0.1:")) {
                        secondAdd = i;
                        break;
                    }
                }
                int trackerPort = Integer.parseInt(order.substring(firstAdd + 10, secondAdd - 1));
                int listenPort = Integer.parseInt(order.substring(secondAdd + 10, order.length()));

                app.getSeeders(fileName, "localhost", trackerPort, "localhost", listenPort);

            } else if (order.equals("request-logs")) {
                requestLogs();
            } else {
                System.out.println("your order is not valid");
            }

        }
    }


    @SneakyThrows
    public static void requestLogs() {
        System.out.println(new String(Files.readAllBytes(Paths.get(Config.getInstance().getProperty("logs")))));
    }
}

package control;

import client.Client;
import common.command.req.FileStat;
import common.command.req.Get;
import common.command.req.GetSeeders;
import common.command.req.Seed;
import common.command.res.Accept;
import common.command.res.OK;
import common.net.data.Entity;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import policies.RetrievalBehaviour;
import policies.Validator;
import server.Server;
import util.Config;
import util.Semaphore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@SuppressWarnings("all")
public class App {
    public boolean alive = true;

    public static App instance = new App();

    private static Logger logger;

    private final Map<Entity, Client> activerTrackers = new HashMap<>();

    private final Map<String, Server> activerListeners = new HashMap<>();

    private final Map<Entity, Client> activeDownloaders = new HashMap<>();

    private final Map<String, Boolean> activeDownloads = new HashMap<>();

    private final Object downloadLock = new Object();

    private final Object linkLock = new Object();

    public App() {
        Config.getInstance().setPath("./src/main/resources/config.properties");
        logger = LogManager.getLogger();
    }

    protected Client getConnection(String serverAddress, int serverPort) {
        var lock = new Semaphore(0);
        var client = new Client();
        client.setConnectionTrigger(lock::forceRelease);
        client.setRemoteName(serverAddress);
        client.setRemotePort(serverPort);
        // TODO : set local port as well
        client.setRetrievalAction(new RetrievalBehaviour(client, new Validator()));
        client.init();
        client.connect();
        lock.forceLock();
        return client;
    }

    protected void startListening(String fileName, int localPort) {
        var server = new Server();
        server.setPortNumber(localPort);
       synchronized (linkLock) {
           instance.activerListeners.put(fileName, server);
       }
        server.setRetrievalAction(new RetrievalBehaviour(server, new Validator()));
        server.init();
        server.start();
    }

    @SneakyThrows
    public void shareFile(String fileName, String trackerIp, int trackerPort, int localPort) {
        startListening(fileName, localPort);
        var client = getConnection(trackerIp, trackerPort);
        synchronized (linkLock) {
            instance.activerTrackers.put(client.getServer(), client);
        }
        client.send(new Seed(client.getServer()).addHeader("connection-type", "udp").addHeader("type", "seed")
                .addHeader("file-name", fileName).addHeader("address", InetAddress.getByName("localhost"))
                .addHeader("port", localPort));
    }

    public void fileShared(Entity server, String fileName) {
        synchronized (linkLock) {
            // instance.activerTrackers.get(server).disconnect();
            System.out.println("file " + fileName + " shared successfully");
            logger.info("file " + fileName + " shared");
        }
    }

    public void downloadSucccessful(String fileName) {
        synchronized (downloadLock) {
            activeDownloads.put(fileName, true);
        }
    }

    public void pickSeeder(Entity server, String fileName, List<AbstractMap.SimpleEntry> list) {

        synchronized (downloadLock) {
            activeDownloads.put(fileName, false);
        }

        synchronized (linkLock) {
            var connection = instance.activerTrackers.get(server);
            // connection.disconnect();
            var timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override @SneakyThrows
                public void run() {
                    var bool = activeDownloads.get(fileName);
                    connection.send(new FileStat(server).addHeader("file-name", fileName).addHeader("result", bool)
                            .addHeader("local-address", InetAddress.getByName("localhost")));
                }
            }, 30000);
        }

        if (list.isEmpty()) {
            System.out.println("No seeders for file : " + fileName + " were found!");
            logger.error("no seeders for " + fileName + " were found!");
            return;
        }

        AbstractMap.SimpleEntry<String, Integer> target = list.get(0);
        Client client =  null;
        synchronized (linkLock) {
            client = getConnection(target.getKey(), target.getValue());
            instance.activeDownloaders.put(client.getServer(), client);
        }
        client.send(new Get(client.getServer()).addHeader("file-name", fileName));
        logger.info("file " + fileName + " was requested, result : \n" + list);
    }

    @SneakyThrows
    public void getSeeders(String fileName, String remoteAdrress, int remotePort, String localAddress, int localPort) {
        var client = getConnection(remoteAdrress, remotePort);
        synchronized (linkLock) {
            instance.activerTrackers.put(client.getServer(), client);
        }
        client.send(new GetSeeders(client.getServer()).addHeader("connection-type", "udp")
                .addHeader("type", "get-seeders").addHeader("file-name", fileName)
                .addHeader("address", InetAddress.getByName("localhost"))
                .addHeader("port", localPort));
    }

    public void offerFile(String fileName, Entity client) {
        Server server = null;
        synchronized (linkLock) {
            server = instance.activerListeners.get(fileName);
        }

        if (server == null)
            return;

        server.send(new OK(client).addHeader("file-name", fileName));
        server.send(new Accept(client).addHeader("file-name", fileName)
                .addHeader("content", getBytes(fileName)));
    }

    public void acceptFile(String fileName, byte[] content, Entity sender) {
        synchronized (linkLock) {
            instance.activeDownloaders.get(sender).disconnect();
        }
        writeByte(content, fileName);
    }

    @SneakyThrows
    protected static byte[] getBytes(String pathString) {
        Path path = Paths.get(Config.getInstance().getProperty("outDir") + pathString);
        return Files.readAllBytes(path);
    }

    @SneakyThrows
    public static void writeByte(byte[] bytes , String pathString) {
        File file = new File(Config.getInstance().getProperty("inDir") + pathString);
        OutputStream os = new FileOutputStream(file , true);
        os.write(bytes);
        os.close();
    }
}

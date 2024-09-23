package common.net.rdp;

import common.net.data.Command;
import common.net.data.Packet;
import common.net.udp.UDPConnection;
import util.Semaphore;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RDPConnection extends UDPConnection {
    protected final Queue<Packet> inbound = new ConcurrentLinkedQueue<>();
    final Semaphore read_lock = new Semaphore(0);
    protected long last_seq_wrt = -1;
    protected final Packet[] out_arr = new Packet[2000];
    HouseKeeper keeper;


    @Override
    public void connectTo(String remoteAddress, int remotePort, String localAddress, int localPort) throws IOException {
        super.connectTo(remoteAddress, remotePort, localAddress, localPort);
        start();
    }
    protected void start() {
        keeper = new  HouseKeeper(this, readBuffer, out_arr, inbound, read_lock);
        keeper.start();
    }

    @Override
    public void disconnect() throws IOException {
        super.disconnect();
        keeper.kill();
    }

    @Override
    public void send(Packet data) throws IOException {
        long current = (last_seq_wrt + 1) % 2000;
        ensure(data, current);
        last_seq_wrt = current;
        data.id = current;
        out_arr[(int) current] = data;
        transmit(data);
    }

    protected void ensure(Packet data, long current) throws IOException {
        boolean notFlushed = false;
        for (int i = (int) current; i < 2000; i++) {
            if (out_arr[i] != null) {
                notFlushed = true;
                break;
            }
        }
        if (notFlushed) {
            flush((int) current);
            send(data);
        }
    }

    protected void flush(int index) throws IOException {
        for (int i  = index; i < 2000; i++) {
            if (out_arr[i] != null) {
                transmit(out_arr[i]);
                try {
                    Thread.sleep(5);
                } catch (Exception ignored) {}
            }
        }
    }



    void transmit(Packet data) throws IOException {
        super.send(data);
    }

    void send(byte[] pack) throws IOException {
        var packet = new DatagramPacket(pack, pack.length);
        socket.send(packet);
    }

    @Override
    public  Packet fetch() throws IOException { // should be called by a single thread
        Packet packet;
        while ((packet = inbound.poll()) == null)
            read_lock.forceLock();
        return packet;
    }

    @Override
    protected void handShake() throws IOException {
        var timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    transmit(new Packet(new Command(null).addHeader("syn", "syn"), -1));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        },0, 500);
        var universalPacket = Packet.generateUniversalPacket(readPacket());
        while (universalPacket.id != -1 || !universalPacket.command.headers.containsKey("ack"))
            universalPacket = Packet.generateUniversalPacket(readPacket());
        timer.cancel();
    }
}

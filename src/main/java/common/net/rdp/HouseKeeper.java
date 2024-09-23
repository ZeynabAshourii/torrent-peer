package common.net.rdp;

import common.net.data.Packet;
import common.net.udp.ByteUtils;
import util.Semaphore;

import java.io.IOException;
import java.util.Queue;

public class HouseKeeper extends Thread{
    private final RDPConnection connection;
    private final byte[] readBuffer;
    private final byte[] long_bytes = new byte[8];
    private final Packet[] out_arr;
    private final Queue<Packet> inbound;
    private final byte[] ack_buff = new byte[11];
    private final Semaphore read_lock;
    private long last_seq_rec = -1;
    private boolean killed;


    public HouseKeeper(RDPConnection connection, byte[] readBuffer, Packet[] out_arr, Queue<Packet> inbound, Semaphore read_lock) {
        this.connection = connection;
        this.readBuffer = readBuffer;
        this.out_arr = out_arr;
        this.inbound = inbound;
        this.read_lock = read_lock;
    }

    public void run() {
        while (!killed) {
            try {
                var read = connection.readPacket();

                // determine the purpose

                if (readBuffer[0] == 0 && readBuffer[1] == 0) {
                    System.arraycopy(readBuffer, 3, long_bytes, 0, 8);
                    long seq_num = ByteUtils.bytesToLong(long_bytes);
                    out_arr[(int)seq_num] = null;
                }

                else {
                    var packet = Packet.generateUniversalPacket(read);
                    if (packet != null && packet.id == (last_seq_rec + 1) % 2000) {
                        inbound.add(packet);
                        connection.send(ByteUtils.longToBytes(packet.id, ack_buff));
                        last_seq_rec = packet.id;
                        read_lock.forceRelease();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    void kill() {
        killed = true;
    }
}

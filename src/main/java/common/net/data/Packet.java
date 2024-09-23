package common.net.data;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import common.net.udp.ByteUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.net.DatagramPacket;

public class Packet implements Serializable {

    public Command command;
    public long id;
    @Getter
    @Setter
    public transient Entity entity;

    public Packet(Command command, long id) {
        this.command = command;
        this.id = id;
    }

    public static byte[] toBytes(Packet input) {
        var gson = new Gson();
        return gson.toJson(input).getBytes();
    }

    public static Packet toPacket(byte[] input, int bound) {
        Gson gson = new Gson();
        String substring = new String(input).substring(2, 2 + bound);
        try {
            return gson.fromJson(substring, Packet.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Malformed packet : " + substring);
            e.printStackTrace();
        }
        return new Packet(new Command(null), -1);
    }

    public static Packet toPacket(Object input) {
        try {
            return (Packet) input;
        } catch (ClassCastException e) {
            throw new RuntimeException("Packet is corrupted");
        }
    }

    public static Packet generateUniversalPacket(DatagramPacket datagramPacket) {
        return Packet.toPacket(datagramPacket.getData(), ByteUtils.toHex(datagramPacket.getData()));
    }
}

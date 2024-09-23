package common.net.udp;

public class ByteUtils {
    public static synchronized byte[] longToBytes(long lng, byte[] out) { // only to be used for ack and rtt not the mainstream send
        out[3] = (byte) lng;
        out[4] = (byte) (lng >> 8);
        out[5] = (byte) (lng >> 16);
        out[6] = (byte) (lng >> 24);
        out[7] = (byte) (lng >> 32);
        out[8] = (byte) (lng >> 40);
        out[9] = (byte) (lng >> 48);
        out[10] = (byte) (lng >> 56);
        return out;
    }

    public static long bytesToLong(byte[] b) {
        return ((long) b[7] << 56)
                | ((long) b[6] & 0xff) << 48
                | ((long) b[5] & 0xff) << 40
                | ((long) b[4] & 0xff) << 32
                | ((long) b[3] & 0xff) << 24
                | ((long) b[2] & 0xff) << 16
                | ((long) b[1] & 0xff) << 8
                | ((long) b[0] & 0xff);
    }
    public static byte[] toBytes(int length) {
        byte[] out = new byte[2];
        int ind1 = length / 256;
        int ind2 = length - 256 * ind1;
        out[0] = (byte) ind1;
        out[1] = (byte) ind2;
        return out;
    }

    public static int toHex(byte[] input) {
        return (input[0] & 0xFF) * 256 + (input[1] & 0xFF);
    }
}

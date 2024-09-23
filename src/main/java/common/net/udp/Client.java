package common.net.udp;


import common.net.data.Command;
import common.net.data.Packet;


public class Client extends Thread{
    public void run() {
        try {
            var connection = new UDPConnection();
            connection.connectTo("localhost", 9000, "localhost", 8000);
            /*while (true) {
                var packet = connection.fetch();
                System.out.println(packet.command.headers);
                var k = packet.command.headers.keySet().toArray()[0];
                var v = (double) packet.command.getHeader(k);
                System.out.println("client : " + v);
                var newPacket = new Packet(new Command(null).addHeader(k, v + 1), 10);
                connection.send(newPacket);
            }*/
            while (true) {
                connection.send(new Packet(new Command(null).addHeader("shit", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed eu porta augue, a placerat est. Sed elementum nulla vitae vulputate euismod. Curabitur elit arcu, eleifend sed scelerisque in, ornare convallis leo. Donec fermentum venenatis leo, vitae sodales orci dictum non. Suspendisse cursus ligula mauris, at malesuada eros elementum vel. Suspendisse interdum sodales elit. Duis tempus orci commodo vulputate sodales. Donec sit amet volutpat eros, id vehicula orci. Suspendisse in leo at nisl condimentum tempor quis eget mauris. Vivamus faucibus nulla a quam condimentum, vitae vulputate eros efficitur. Maecenas laoreet dolor vitae nulla elementum tincidunt. Vivamus eget est elit. Sed gravida maximus gravida. Nam posuere porttitor tristique. Quisque urna nisl, molestie vel urna in, rutrum luctus nibh."), 1));
                connection.send(new Packet(new Command(null).addHeader("shit", "Cras ut enim dignissim, condimentum neque eget, rhoncus tortor. Quisque sapien libero, mattis id eleifend cursus, tristique at sem. Curabitur scelerisque venenatis porttitor. Duis viverra sit amet nibh a accumsan. Nam varius enim urna, et tempus ante semper et. Nullam at nulla ac est elementum malesuada eu at nisi. Suspendisse ultrices luctus mollis. Morbi ac pharetra diam. Mauris quis magna aliquet, scelerisque diam sed, dapibus nulla. Phasellus quis viverra velit. Fusce commodo ex eu erat blandit laoreet. Ut ligula risus, efficitur eget hendrerit id, iaculis quis arcu. Suspendisse at accumsan elit."), 1));
                connection.send(new Packet(new Command(null).addHeader("shit", "Etiam vitae tellus quis ipsum luctus sagittis non eu dui. Integer cursus neque ut ex accumsan pharetra. Nullam fringilla odio lectus, ac congue neque efficitur et. Sed id imperdiet orci, eu cursus dui. Aliquam fermentum pulvinar orci, et blandit massa elementum non. Donec elementum tempus massa, quis suscipit nunc semper nec. Cras eget purus ut mauris tempor congue."), 1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

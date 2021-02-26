import cli.Console;
import net.LibraryUdpClient;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpClient2 {

    public static void main(String[] args) {
        try (DatagramSocket datagramSocket = new DatagramSocket(9011)) {
            LibraryUdpClient client = new LibraryUdpClient(new Console(), datagramSocket, InetAddress.getByName("localhost"), 9001);
            client.init();
            client.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

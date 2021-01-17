import businessLogic.MediaAdmin;
import businessLogic.MediaLibraryAdmin;
import crud.CRUD;
import crud.MediaCRUD;
import crud.UploaderCRUD;
import mediaDB.MediaContent;
import mvc.Console;
import server.LibraryUdpServer;
import server.TcpServerSession;
import storage.MediaStorage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class LibraryServer {

    private static void startTcpServer(MediaAdmin mediaAdmin) {
        try (ServerSocket serverSocket = new ServerSocket(9000)) {
            while (true) {
                Socket socket = serverSocket.accept();
                TcpServerSession s = new TcpServerSession(socket, mediaAdmin);
                System.out.println("new client@" + socket.getInetAddress() + ":" + socket.getPort());
                new Thread(s).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startUdpServer(MediaAdmin mediaAdmin) {
        try (DatagramSocket datagramSocket = new DatagramSocket(9001)) {
            LibraryUdpServer s = new LibraryUdpServer(datagramSocket, mediaAdmin);
            while (true) {
                try {
                    s.processMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Console console = new Console();
        long diskSizeGB = console.readLongFromStdin("Enter Disk size in gigabyte: ");
        MediaStorage mediaStorage = new MediaStorage(diskSizeGB * 1000);
        // create media admin
        CRUD<MediaContent> mediaCRUD = new MediaCRUD();
        UploaderCRUD uploaderCRUD = new UploaderCRUD();
        MediaAdmin mediaAdmin = new MediaLibraryAdmin(mediaStorage, uploaderCRUD, mediaCRUD);

        while (true) {
            String protocol = console.readStringFromStdin("Select UDP or TCP: ");
            switch (protocol) {
                case "UDP":
                    startUdpServer(mediaAdmin);
                    return;
                case "TCP":
                    startTcpServer(mediaAdmin);
                    return;
            }
        }
    }
}

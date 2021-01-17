import businessLogic.MediaAdmin;
import businessLogic.MediaLibraryAdmin;
import crud.CRUD;
import crud.MediaCRUD;
import crud.UploaderCRUD;
import mediaDB.MediaContent;
import mvc.Console;
import server.TcpServerSession;
import storage.MediaStorage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LibraryServer {

    public static void main(String[] args) {
        Console console = new Console();
        long diskSizeGB = console.readLongFromStdin("Enter Disk size in gigabyte:");
        MediaStorage mediaStorage = new MediaStorage(diskSizeGB * 1000);
        // create media admin
        CRUD<MediaContent> mediaCRUD = new MediaCRUD();
        UploaderCRUD uploaderCRUD = new UploaderCRUD();
        MediaAdmin mediaAdmin = new MediaLibraryAdmin(mediaStorage, uploaderCRUD, mediaCRUD);

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
}

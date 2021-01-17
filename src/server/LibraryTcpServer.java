package server;

import businessLogic.MediaAdmin;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LibraryTcpServer {
    private MediaAdmin mediaAdmin;
    private ServerSocket serverSocket;

    public LibraryTcpServer(ServerSocket serverSocket, MediaAdmin mediaAdmin) {
        this.mediaAdmin = mediaAdmin;
        this.serverSocket = serverSocket;
    }

    public void start() throws IOException {
        while (!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();
            TcpServerSession s = new TcpServerSession(socket, mediaAdmin);
            System.out.println("new client@" + socket.getInetAddress() + ":" + socket.getPort());
            new Thread(s).start();
        }
    }
}

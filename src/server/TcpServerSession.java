package server;

import businessLogic.MediaAdmin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TcpServerSession extends ServerSession implements Runnable {

    private final Socket socket;
    public TcpServerSession(Socket socket, MediaAdmin mediaAdmin) {
        super(mediaAdmin);
        this.socket = socket;
    }

    @Override
    public void run() {
        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
                System.out.println("client@" + socket.getInetAddress() + ":" + socket.getPort() + " connected");
                do {
                    String response = executeSession(in);
                    out.writeUTF(response);
                } while (true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

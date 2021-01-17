package server;

import businessLogic.MediaAdmin;

import java.io.DataInputStream;
import java.io.IOException;

public class UdpServerSession extends ServerSession {

    private byte[] inBuffer = new byte[1024];
    int sessionId;

    public UdpServerSession(MediaAdmin mediaAdmin) {
        super(mediaAdmin);
    }

    public String processMessage(DataInputStream dis) throws IOException {
        return executeSession(dis);
    }
}

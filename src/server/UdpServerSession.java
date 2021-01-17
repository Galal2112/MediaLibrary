package server;

import businessLogic.MediaAdmin;

import java.io.DataInputStream;
import java.io.IOException;

public class UdpServerSession extends ServerSession {

    int sessionId;

    public UdpServerSession(MediaAdmin mediaAdmin) {
        super(mediaAdmin);
    }

    public String processMessage(DataInputStream dis) throws IOException {
        return executeSession(dis);
    }
}

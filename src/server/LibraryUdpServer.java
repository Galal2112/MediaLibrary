package server;

import businessLogic.MediaAdmin;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class LibraryUdpServer {
    private DatagramSocket socket;
    private byte[] inBuffer = new byte[1024];
    private HashMap<Integer, UdpServerSession> sessions = new HashMap<>();
    private int sessionCounter = 0;
    private MediaAdmin mediaAdmin;

    public LibraryUdpServer(DatagramSocket socket, MediaAdmin mediaAdmin) {
        this.socket = socket;
        this.mediaAdmin = mediaAdmin;
    }

    public void processMessage() throws IOException {
        DatagramPacket packetIn = new DatagramPacket(this.inBuffer, this.inBuffer.length);
        this.socket.receive(packetIn);
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packetIn.getData()))) {
            this.processMessage(dis, packetIn.getAddress(), packetIn.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMessage(DataInputStream dis, InetAddress address, int port) throws IOException {
        char request = dis.readChar();
        switch (request) {
            case 'I':
                UdpServerSession session = new UdpServerSession(mediaAdmin);
                session.sessionId = ++this.sessionCounter;
                ByteArrayOutputStream bos = new ByteArrayOutputStream(Integer.BYTES);
                DataOutputStream dos = new DataOutputStream(bos);
                dos.writeInt(session.sessionId);
                byte[] out = bos.toByteArray();
                DatagramPacket packetOut = new DatagramPacket(out, out.length, address, port);
                this.socket.send(packetOut);
                this.sessions.put(session.sessionId, session);
                System.out.println("open session " + session.sessionId + " for " + address + ":" + port);
                break;
            case 'N':
                UdpServerSession targetSession = this.sessions.get(dis.readInt());
                System.out.println("New message from session with id: " + targetSession.sessionId);
                String response = targetSession.processMessage(dis);
                bos = new ByteArrayOutputStream(response.getBytes().length);
                dos = new DataOutputStream(bos);

                dos.writeUTF(response);
                out = bos.toByteArray();
                packetOut = new DatagramPacket(out, out.length, address, port);
                this.socket.send(packetOut);
                break;
            case 'S':
                this.sessions.remove(dis.readInt());
                System.out.println("close session for " + address + ":" + port);
                break;
        }
    }

}

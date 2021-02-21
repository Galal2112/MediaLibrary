package client;

import mvc.Command;
import mvc.Console;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

public class LibraryUdpClient {
    private final List<Command> commandList = Arrays.asList(Command.values());

    private final Console console = new Console();
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private int sessionId;

    public LibraryUdpClient(DatagramSocket datagramSocket, InetAddress serverAddress, int serverPort) {
        this.socket = datagramSocket;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void init() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(14);
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeChar('I');
        dos.close();
        bos.close();
        this.sendMessage(bos.toByteArray());
        DataInputStream dis = receive();
        this.sessionId = dis.readInt();
    }

    public void run() throws IOException {
        System.out.println("Media Library available commands:");
        for (Command command : commandList) {
            System.out.println(command.toString());
        }

        while (true) {
            String input = console.readStringFromStdin(">> ");
            int bytes = input.getBytes().length + Integer.BYTES + 1;
            ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes);
            DataOutputStream dos = new DataOutputStream(bos);
            if (input.equals("exit")) {
                dos.writeChar('S');
                dos.writeInt(sessionId);
                return;
            } else {
                dos.writeChar('N');
                dos.writeInt(sessionId);
                dos.writeUTF(input);
            }
            dos.close();
            bos.close();
            sendMessage(bos.toByteArray());
            DataInputStream dis = receive();
            String response = dis.readUTF();
            System.out.println(response);
        }
    }

    private void sendMessage(byte[] message) {
        DatagramPacket packetOut = new DatagramPacket(message, message.length, this.serverAddress, this.serverPort);
        try {
            this.socket.send(packetOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DataInputStream receive() throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packetIn = new DatagramPacket(buffer, buffer.length);
        this.socket.receive(packetIn);
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packetIn.getData()));
        return dis;
    }
}


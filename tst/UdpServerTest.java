import businessLogic.MediaAdmin;
import model.InteractiveVideoImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import server.LibraryUdpServer;
import storage.InsufficientStorageException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UdpServerTest {

    @Test
    public void testInsert() throws IOException {
        MediaAdmin mediaAdmin = Mockito.mock(MediaAdmin.class);
        DatagramSocket mockSocket = mock(DatagramSocket.class);
        ArgumentCaptor<DatagramPacket> captor = ArgumentCaptor.forClass(DatagramPacket.class);
        LibraryUdpServer server = new LibraryUdpServer(mockSocket, mediaAdmin);
        InetAddress address = mock(InetAddress.class);
        int port = 9000;
        doAnswer(new Answer<Void>() {
            int count = 0;

            public Void answer(InvocationOnMock invocation) throws IOException {
                if (count == 0) {
                    initPacket(invocation, address, port);
                } else if (count == 1) {
                    updatePacketWithCommand(":c", invocation, address, port);
                } else if (count == 2) {
                    updatePacketWithCommand("Produzent1", invocation, address, port);
                } else if (count == 3) {
                    String uploadCommand = "InteractiveVideo Produzent1 Lifestyle,News 5000 3600 DWT 640 480 Abstimmung";
                    updatePacketWithCommand(uploadCommand, invocation, address, port);
                } else {
                    exitPacket(invocation, address, port);
                }
                count++;
                return null;
            }
        }).when(mockSocket).receive(any(DatagramPacket.class));

        int count = 0;
        while (++count < 5) {
            try {
                server.processMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        verify(mediaAdmin).createUploader(any());
        try {
            verify(mediaAdmin).upload(any(InteractiveVideoImpl.class));
        } catch (InsufficientStorageException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testList() throws IOException {
        MediaAdmin mediaAdmin = Mockito.mock(MediaAdmin.class);
        DatagramSocket mockSocket = mock(DatagramSocket.class);
        ArgumentCaptor<DatagramPacket> captor = ArgumentCaptor.forClass(DatagramPacket.class);
        LibraryUdpServer server = new LibraryUdpServer(mockSocket, mediaAdmin);
        InetAddress address = mock(InetAddress.class);
        int port = 9000;
        doAnswer(new Answer<Void>() {
            int count = 0;

            public Void answer(InvocationOnMock invocation) throws IOException {
                if (count == 0) {
                    initPacket(invocation, address, port);
                } else if (count == 1) {
                    updatePacketWithCommand(":c", invocation, address, port);
                } else if (count == 2) {
                    updatePacketWithCommand("Produzent1", invocation, address, port);
                } else if (count == 3) {
                    String uploadCommand = "InteractiveVideo Produzent1 Lifestyle,News 5000 3600 DWT 640 480 Abstimmung";
                    updatePacketWithCommand(uploadCommand, invocation, address, port);
                } else if (count == 4) {
                    updatePacketWithCommand(":r", invocation, address, port);
                } else if (count == 5) {
                    updatePacketWithCommand("content", invocation, address, port);
                } else {
                    exitPacket(invocation, address, port);
                }
                count++;
                return null;
            }
        }).when(mockSocket).receive(any(DatagramPacket.class));

        int count = 0;
        while (++count < 7) {
            try {
                server.processMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        verify(mediaAdmin).listMedia(null);
    }

    @Test
    public void testDelete() throws IOException {
        MediaAdmin mediaAdmin = Mockito.mock(MediaAdmin.class);
        DatagramSocket mockSocket = mock(DatagramSocket.class);
        ArgumentCaptor<DatagramPacket> captor = ArgumentCaptor.forClass(DatagramPacket.class);
        LibraryUdpServer server = new LibraryUdpServer(mockSocket, mediaAdmin);
        InetAddress address = mock(InetAddress.class);
        int port = 9000;
        doAnswer(new Answer<Void>() {
            int count = 0;

            public Void answer(InvocationOnMock invocation) throws IOException {
                if (count == 0) {
                    initPacket(invocation, address, port);
                } else if (count == 1) {
                    updatePacketWithCommand(":c", invocation, address, port);
                } else if (count == 2) {
                    updatePacketWithCommand("Produzent1", invocation, address, port);
                } else if (count == 3) {
                    String uploadCommand = "InteractiveVideo Produzent1 Lifestyle,News 5000 3600 DWT 640 480 Abstimmung";
                    updatePacketWithCommand(uploadCommand, invocation, address, port);
                } else if (count == 4) {
                    updatePacketWithCommand(":d", invocation, address, port);
                } else if (count == 5) {
                    updatePacketWithCommand("Produzent1", invocation, address, port);
                } else {
                    exitPacket(invocation, address, port);
                }
                count++;
                return null;
            }
        }).when(mockSocket).receive(any(DatagramPacket.class));

        int count = 0;
        while (++count < 7) {
            try {
                server.processMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        verify(mediaAdmin).deleteUploaderByName(any());
    }

    private void updatePacketWithCommand(String command, InvocationOnMock invocation, InetAddress address, int port) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeChar('N');
        dos.writeInt(1);
        dos.writeUTF(command);
        dos.close();
        bos.close();
        DatagramPacket d = invocation.getArgument(0);
        d.setAddress(address);
        d.setPort(port);
        d.setData(bos.toByteArray());
    }

    private void initPacket(InvocationOnMock invocation, InetAddress address, int port) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(14);
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeChar('I');
        dos.close();
        bos.close();
        DatagramPacket d = invocation.getArgument(0);
        d.setAddress(address);
        d.setPort(port);
        d.setData(bos.toByteArray());
    }

    private void exitPacket(InvocationOnMock invocation, InetAddress address, int port) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(14);
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeChar('I');
        dos.writeInt(1);
        dos.close();
        bos.close();
        DatagramPacket d = invocation.getArgument(0);
        d.setAddress(address);
        d.setPort(port);
        d.setData(bos.toByteArray());
    }
}

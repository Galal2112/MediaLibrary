import businessLogic.MediaAdmin;
import model.InteractiveVideoImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import server.LibraryTcpServer;
import storage.InsufficientStorageException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class TcpServerTest {

    private ServerSocket serverSocket;
    private Socket socket;
    private DataOutputStream dos;

    @BeforeEach
    void setup() throws IOException {
        serverSocket = Mockito.mock(ServerSocket.class);
        socket = Mockito.mock(Socket.class);
        when(serverSocket.accept()).thenReturn(socket);
        InetAddress inetAddress = mock(InetAddress.class);
        when(inetAddress.toString()).thenReturn("Address");
        when(socket.getInetAddress()).thenReturn(inetAddress);
        when(socket.getPort()).thenReturn(1000);
    }

    @Test
    void testInsert() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        dos = new DataOutputStream(bos);
        dos.writeUTF(":c");
        dos.writeUTF("Produzent1");
        dos.writeUTF("InteractiveVideo Produzent1 Lifestyle,News 5000 3600 DWT 640 480 Abstimmung");
        dos.writeUTF("exit");
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(bos.toByteArray()));
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);
        when(socket.getOutputStream()).thenReturn(outStream);

        when(serverSocket.isClosed()).thenAnswer(new Answer<>() {
            int count = 0;
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (count++ == 0) {
                    return false;
                }
                return true;
            }
        });
        MediaAdmin mediaAdmin = Mockito.mock(MediaAdmin.class);
        new LibraryTcpServer(serverSocket, mediaAdmin).start();
        sleep(1);
        verify(mediaAdmin).createUploader(any());
        try {
            verify(mediaAdmin).upload(any(InteractiveVideoImpl.class));
        } catch (InsufficientStorageException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testList() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        dos = new DataOutputStream(bos);
        dos.writeUTF(":c");
        dos.writeUTF("Produzent1");
        dos.writeUTF("InteractiveVideo Produzent1 Lifestyle,News 5000 3600 DWT 640 480 Abstimmung");
        dos.writeUTF(":r");
        dos.writeUTF("content");
        dos.writeUTF("exit");
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(bos.toByteArray()));
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);
        when(socket.getOutputStream()).thenReturn(outStream);

        when(serverSocket.isClosed()).thenAnswer(new Answer<>() {
            int count = 0;
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (count++ == 0) {
                    return false;
                }
                return true;
            }
        });
        MediaAdmin mediaAdmin = Mockito.mock(MediaAdmin.class);
        new LibraryTcpServer(serverSocket, mediaAdmin).start();
        sleep(1);
        verify(mediaAdmin).listMedia(null);
    }

    @Test
    void testDelete() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        dos = new DataOutputStream(bos);
        dos.writeUTF(":c");
        dos.writeUTF("Produzent1");
        dos.writeUTF("InteractiveVideo Produzent1 Lifestyle,News 5000 3600 DWT 640 480 Abstimmung");
        dos.writeUTF(":d");
        dos.writeUTF("Produzent1");
        dos.writeUTF("exit");
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(bos.toByteArray()));
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);
        when(socket.getOutputStream()).thenReturn(outStream);

        when(serverSocket.isClosed()).thenAnswer(new Answer<>() {
            int count = 0;
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (count++ == 0) {
                    return false;
                }
                return true;
            }
        });
        MediaAdmin mediaAdmin = Mockito.mock(MediaAdmin.class);
        new LibraryTcpServer(serverSocket, mediaAdmin).start();
        sleep(1);
        verify(mediaAdmin).deleteUploaderByName(any());
    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

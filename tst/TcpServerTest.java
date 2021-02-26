import businessLogic.MediaAdmin;
import cli.Console;
import model.InteractiveVideoImpl;
import net.LibraryTcpClient;
import net.LibraryTcpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import storage.InsufficientStorageException;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class TcpServerTest {

    private ServerSocket serverSocket;
    private Socket socket;
    private DataOutputStream dos;
    private final PrintStream systemOut = System.out;

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

    @Test
    void testTcpClintPrintsCommandsList() throws IOException {
        Console console = Mockito.mock(Console.class);
        when(console.readStringFromStdin(anyString())).thenReturn("exit");
        Socket socket = Mockito.mock(Socket.class);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{}));
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);
        when(socket.getOutputStream()).thenReturn(outStream);
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        LibraryTcpClient tcpClient = new LibraryTcpClient(console, socket);
        tcpClient.run();
        System.out.println(outContent.toString().trim());
        String expectedOutput = "Media Library available commands:\n" +
                ":c Wechsel in den Einfügemodus\n" +
                ":r Wechsel in den Anzeigemodus\n" +
                ":d Wechsel in den Löschmodus\n" +
                ":u Wechsel in den Änderungsmodus\n" +
                ":p Wechsel in den Persistenzmodus";
        assertTrue(outContent.toString().trim().contains(expectedOutput));
    }

    @Test
    void testTcpClintExitEvent() throws IOException {
        Console console = Mockito.mock(Console.class);
        when(console.readStringFromStdin(anyString())).thenReturn("exit");
        Socket socket = Mockito.mock(Socket.class);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{}));
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);
        when(socket.getOutputStream()).thenReturn(outStream);
        LibraryTcpClient tcpClient = new LibraryTcpClient(console, socket);
        tcpClient.run();
        assertTrue(outStream.toString(StandardCharsets.UTF_8).contains("exit"));
        verify(socket).close();
    }

    @AfterEach
    void tearDown() {
        System.setOut(systemOut);
    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

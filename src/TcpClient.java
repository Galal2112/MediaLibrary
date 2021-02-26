import cli.Console;
import net.LibraryTcpClient;

import java.io.IOException;
import java.net.Socket;

public class TcpClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9000);
        Console console = new Console();
        new LibraryTcpClient(console, socket).run();
    }
}

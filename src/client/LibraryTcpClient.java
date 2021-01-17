package client;

import mvc.Command;
import mvc.Console;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class LibraryTcpClient {
    private final ArrayList<Command> commandList = new ArrayList<>(Arrays.asList(Command.CREATE, Command.DELETE,
            Command.VIEW));

    public void run() {
        Console console = new Console();
        try (Socket socket = new Socket("localhost", 9000);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            System.out.println("Media Library available commands:");
            for (Command command : commandList) {
                System.out.println(command.toString());
            }

            while (true) {
                String input = console.readStringFromStdin(">> ");
                if (input.equals("exit")) {
                    socket.close();

                    return;
                }
                out.writeUTF(input);
                String response = in.readUTF();
                System.out.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

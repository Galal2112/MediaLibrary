package net;

import cli.Command;
import cli.Console;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class LibraryTcpClient {
    private final List<Command> commandList = Arrays.asList(Command.values());

    public void run() {
        Console console = new Console();
        try (Socket socket = new Socket("localhost", 9000);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            System.out.println("Media Library available commands:");
            for (Command command : commandList) {
                if (command == Command.CONFIG) {
                    continue;
                }
                System.out.println(command.toString());
            }

            while (true) {
                String input = console.readStringFromStdin(">> ");
                out.writeUTF(input);
                if (input.equals("exit")) {
                    socket.close();
                    return;
                } else {
                    String response = in.readUTF();
                    System.out.println(response);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package mvc;

import events.InputEvent;
import events.InputEventHandler;

import java.util.List;

public class CliMediaView implements MediaView {

    public static final String CONSOLE_COLOR_RED = "\u001B[31m";
    public static final String CONSOLE_COLOR_RESET = "\033[0m";  // Text Reset
    private final Console console;
    private InputEventHandler handler;

    public CliMediaView(Console console) {
        this.console = console;
    }

    @Override
    public void setHandler(InputEventHandler handler) {
        this.handler = handler;
    }

    @Override
    public void displayCommands(String headLine, List<Command> commands) {
        System.out.println(headLine);
        for (Command command : commands) {
            System.out.println(command.toString());
        }
    }

    @Override
    public void displayError(String error) {
        System.out.println(CONSOLE_COLOR_RED + "Error: " + error + CONSOLE_COLOR_RESET);
    }

    @Override
    public void readInput(String title) {
        String input = console.readStringFromStdin(title);
        InputEvent e = new InputEvent(this, input);
        if (handler != null) {
            handler.handle(e);
        }
    }

    @Override
    public void displayUploader(String name, int uploadsCount) {
        System.out.println(name + " " + uploadsCount);
    }
}

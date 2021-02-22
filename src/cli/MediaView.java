package cli;

import cli.events.InputEventHandler;

import java.util.Date;
import java.util.List;

public interface MediaView {

    void displayCommands(String headLine, List<Command> commands);
    void displayError(String error);
    void displayMessage(String message);
    void readInput(String title);
    void setHandler(InputEventHandler handler);
    void displayUploader(String name, int uploadsCount);
    void displayMedia(String[] types, String[] retrievalAddress, Date[] uploadDate, long[] accessCount);
}

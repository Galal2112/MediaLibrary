package mvc;

import events.InputEventHandler;

import java.util.List;

public interface MediaView {

    void displayCommands(String headLine, List<Command> commands);
    void displayError(String error);
    void readInput(String title);
    void setHandler(InputEventHandler handler);
    void displayUploader(String name, int uploadsCount);
}

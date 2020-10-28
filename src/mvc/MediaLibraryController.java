package mvc;

import businessLogic.MediaAdmin;
import events.InputEvent;
import mediaDB.Uploader;
import model.Producer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class MediaLibraryController implements MediaController {

    private MediaView mediaView;
    private MediaAdmin mediaAdmin;
    private final ArrayList<Command> commandList = new ArrayList<>(Arrays.asList(Command.CREATE,Command.DELETE, Command.VIEW,Command.UPDATE));
    private Command currentCommand = null;

    public MediaLibraryController(MediaView mediaView, MediaAdmin mediaAdmin) {
        this.mediaAdmin = mediaAdmin;
        this.mediaView = mediaView;
    }

    @Override
    public void start() {
        while (true) {
            if (currentCommand == null) {
                mediaView.displayCommands("Media Library available commands", commandList);
                mediaView.readInput("Please select command: ");
            } else {
                mediaView.readInput(">> ");
            }
        }
    }

    @Override
    public void onInputEvent(InputEvent event) {
        if (event.getText() == null) {
            mediaView.displayError("Not a vaild input");
            return;
        }
        if(event.getText().equals("exit"))
            System.exit(0);

        if (currentCommand == null) {
            Optional<Command> commandOptional = commandList.stream().filter(c -> c.getKey().equals(event.getText())).findFirst();
            if (commandOptional.isPresent()) {
                currentCommand = commandOptional.get();
            } else {
                mediaView.displayError("Please insert a valid command");
            }
        } else {
            switch (currentCommand) {
                case CREATE:
                    Producer producer = new Producer(event.getText());
                    try {
                        mediaAdmin.createUploader(producer);
                    } catch (IllegalArgumentException e) {
                        mediaView.displayError(e.getMessage());
                    }
                    break;
                case VIEW:
                    if (event.getText().equals("uploader")) {
                        Map<Uploader, Integer> uploadersAndCounts = mediaAdmin.listProducersAndUploadsCount();
                        for (Uploader uploader : uploadersAndCounts.keySet()) {
                            mediaView.displayUploader(uploader.getName(), uploadersAndCounts.get(uploader));
                        }
                    }
                    break;
                default:
                    break;
            }
            currentCommand = null;
        }
    }
}

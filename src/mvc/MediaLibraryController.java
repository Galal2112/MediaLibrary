package mvc;

import businessLogic.MediaAdmin;
import events.ExitEventListener;
import events.InputEvent;
import events.InputEventHandler;
import events.InputEventListener;
import mediaDB.*;
import model.Producer;
import storage.InsufficientStorageException;
import util.MediaParser;
import util.MediaUtil;

import java.util.*;

public class MediaLibraryController implements MediaController {

    private final MediaView mediaView;
    private final MediaAdmin mediaAdmin;
    private final ArrayList<Command> commandList = new ArrayList<>(Arrays.asList(Command.CREATE, Command.DELETE,
            Command.VIEW));
    private Command currentCommand = null;

    public MediaLibraryController(MediaView mediaView, MediaAdmin mediaAdmin) {
        this.mediaAdmin = mediaAdmin;
        this.mediaView = mediaView;

        InputEventHandler handler = new InputEventHandler();
        handler.add(new ExitEventListener());
        handler.add(new ChangeModeInputListener());
        handler.add(new CreateInputListener());
        handler.add(new ViewInputListener());
        handler.add(new DeleteInputListener());
        mediaView.setHandler(handler);
    }

    @Override
    public void start() {
        mediaView.displayCommands("Media Library available commands", commandList);
        while (true) {
            if (currentCommand != null) {
                mediaView.readInput(">> ");
            } else {
                mediaView.readInput("Please select command: ");
            }
        }
    }

    class ChangeModeInputListener implements InputEventListener {
        @Override
        public void onInputEvent(InputEvent event) {
            if (event.getText() == null) {
                mediaView.displayError("Not a valid input");
                return;
            }
            if (currentCommand == null || event.getText().startsWith(":")) {
                Optional<Command> commandOptional = commandList.stream().filter(c -> c.getKey().equals(event.getText())).findFirst();
                if (commandOptional.isPresent()) {
                    currentCommand = commandOptional.get();
                } else {
                    mediaView.displayError("Please insert a valid command");
                }
            }
        }
    }

    class CreateInputListener implements InputEventListener {
        @Override
        public void onInputEvent(InputEvent event) {
            if (event.getText() == null) {
                mediaView.displayError("Not a valid input");
                return;
            }

            if (currentCommand != null && !event.getText().startsWith(":") && currentCommand == Command.CREATE) {
                handleCreateEvent(event);
            }
        }

        private void handleCreateEvent(InputEvent event) {
            String inputText = event.getText();
            String[] parsedString = inputText.split(" ");

            if (parsedString.length == 1) {
                createProducer(event.getText());
                return;
            }

            try {
                UploadableMediaContent mediaContent = MediaParser.parseMedia(event.getText());
                mediaAdmin.upload(mediaContent);
                mediaView.displayMessage("Media uploaded successfully");
            } catch (IllegalArgumentException | InsufficientStorageException e) {
                mediaView.displayError(e.getMessage());
            }
        }

        private void createProducer(String text) {
            Producer producer = new Producer(text);
            try {
                mediaAdmin.createUploader(producer);
            } catch (IllegalArgumentException e) {
                mediaView.displayError(e.getMessage());
            }
        }

    }

    class ViewInputListener implements InputEventListener {
        @Override
        public void onInputEvent(InputEvent event) {
            if (event.getText() == null) {
                mediaView.displayError("Not a valid input");
                return;
            }

            if (currentCommand != null && !event.getText().startsWith(":") && currentCommand == Command.VIEW) {
                if (event.getText().equalsIgnoreCase("uploader")) {
                    viewUploaders();
                } else if (event.getText().toLowerCase().startsWith("content")) {
                    viewContent(event);
                }
            }
        }

        private void viewUploaders() {
            Map<Uploader, Integer> uploadersAndCounts = mediaAdmin.listProducersAndUploadsCount();
            if (uploadersAndCounts.size() == 0) {
                mediaView.displayMessage("No registered uploader");
                return;
            }
            for (Uploader uploader : uploadersAndCounts.keySet()) {
                mediaView.displayUploader(uploader.getName(), uploadersAndCounts.get(uploader));
            }
        }

        private void viewContent(InputEvent event) {
            String[] split = event.getText().split(" ");
            List<?> mediaList;
            if (split.length == 1) {
                mediaList = mediaAdmin.listMedia(null);
            } else {
                String type = split[1];

                Class<? extends UploadableMediaContent> cls = MediaUtil.getMediaClass(type);
                if (cls != null) {
                    mediaList = mediaAdmin.listMedia(cls);
                } else {
                    mediaView.displayError("Unsupported Media type");
                    return;
                }
            }

            String[] retrievalAddress = new String[mediaList.size()];
            Date[] uploadDate = new Date[mediaList.size()];
            long[] accessCount = new long[mediaList.size()];
            for (int i = 0; i < mediaList.size(); i++) {
                Object media = mediaList.get(i);
                if (media instanceof Audio) {
                    Audio audio = (Audio) media;
                    retrievalAddress[i] = audio.getAddress();
                    uploadDate[i] = audio.getUploadDate();
                    accessCount[i] = audio.getAccessCount();
                } else if (media instanceof Video) {
                    Video video = (Video) media;
                    retrievalAddress[i] = video.getAddress();
                    uploadDate[i] = video.getUploadDate();
                    accessCount[i] = video.getAccessCount();
                }
            }
            mediaView.displayMedia(retrievalAddress, uploadDate, accessCount);
        }
    }

    class DeleteInputListener implements InputEventListener {
        @Override
        public void onInputEvent(InputEvent event) {
            if (event.getText() == null) {
                mediaView.displayError("Not a valid input");
                return;
            }

            if (currentCommand != null && !event.getText().startsWith(":") && currentCommand == Command.DELETE) {
                try {
                    mediaAdmin.deleteUploaderByName(event.getText());
                } catch (IllegalArgumentException e) {
                    try {
                        mediaAdmin.deleteMediaByAddress(event.getText());

                    } catch (IllegalArgumentException u) {
                        mediaView.displayError("Invalid Input");
                    }
                }
            }
        }
    }
}

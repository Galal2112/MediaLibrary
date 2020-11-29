package mvc;

import businessLogic.MediaAdmin;
import events.ExitEventListener;
import events.InputEvent;
import events.InputEventHandler;
import events.InputEventListener;
import mediaDB.*;
import model.InteractiveVideoImpl;
import model.LicensedAudioVideoImpl;
import model.Producer;
import storage.InsufficientStorageException;

import java.time.Duration;
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

    private boolean isInteractiveVideo(String mediaType) {
        return mediaType.equals("InteractiveVideo");
    }

    private boolean isLicensedAudioVideo(String mediaType) {
        return mediaType.equals("LicensedAudioVideo");
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
                String mediaType = parsedString[0];
                Producer producer = new Producer(parsedString[1]);
                String[] inputTags = parsedString[2].split(",");
                List<Tag> tags = new ArrayList<>();
                for (String inputTag : inputTags) {
                    try {
                        tags.add(Tag.valueOf(inputTag));
                    } catch (IllegalArgumentException e) {
                        // Non existing tags
                    }
                }

                long bitrate = Long.parseLong(parsedString[3]);
                long durationInSeconds = Long.parseLong(parsedString[4]);
                Duration duration = Duration.ofSeconds(durationInSeconds);

                String videoEncoding = parsedString[5];
                int height = Integer.parseInt(parsedString[6]);
                int width = Integer.parseInt(parsedString[7]);

                if (isInteractiveVideo(mediaType)) {
                    InteractiveVideo interactiveVideo = new InteractiveVideoImpl(mediaType, width, height,
                            videoEncoding, bitrate, duration, producer);
                    interactiveVideo.setTags(tags);
                    mediaAdmin.upload(interactiveVideo);
                } else if (isLicensedAudioVideo(mediaType)) {
                    String audioEncoding = parsedString[8];
                    int samplingRate = Integer.parseInt(parsedString[9]);
                    String holder = parsedString[10];
                    LicensedAudioVideo licensedAudioVideo = new LicensedAudioVideoImpl(samplingRate, width, height,
                            audioEncoding, holder, bitrate, duration, producer);
                    licensedAudioVideo.setTags(tags);
                    mediaAdmin.upload(licensedAudioVideo);
                } else {
                    mediaView.displayError("Unsupported Media type");
                }
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                mediaView.displayError("Invalid insert command");
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
                if (isInteractiveVideo(type)) {
                    mediaList = mediaAdmin.listMedia(InteractiveVideo.class);
                } else if (isLicensedAudioVideo(type)) {
                    mediaList = mediaAdmin.listMedia(LicensedAudioVideo.class);
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

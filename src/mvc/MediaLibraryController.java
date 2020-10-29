package mvc;

import businessLogic.MediaAdmin;
import events.InputEvent;
import mediaDB.LicensedAudioVideo;
import mediaDB.Uploader;
import model.InteractiveVideoImpl;
import model.LicensedAudioVideoImpl;
import model.Producer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class MediaLibraryController implements MediaController {

    private final MediaView mediaView;
    private final MediaAdmin mediaAdmin;
    private final ArrayList<Command> commandList = new ArrayList<>(Arrays.asList(Command.CREATE, Command.DELETE,
            Command.VIEW,Command.UPDATE));
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
                    handleCreateEvent(event);
                    break;
                case VIEW:
                    if (event.getText().equals("uploader")) {
                        viewUploaders();
                    }
                    break;
                default:
                    break;
            }
            currentCommand = null;
        }
    }

    private void viewUploaders() {
        Map<Uploader, Integer> uploadersAndCounts = mediaAdmin.listProducersAndUploadsCount();
        for (Uploader uploader : uploadersAndCounts.keySet()) {
            mediaView.displayUploader(uploader.getName(), uploadersAndCounts.get(uploader));
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
            String[] tags = parsedString[2].split(",");
            long bitrate = Long.parseLong(parsedString[3]);
            long durationInSeconds = Long.parseLong(parsedString[4]);
            Duration duration = Duration.ofSeconds(durationInSeconds);

            String videoEncoding = parsedString[5];
            int height = Integer.parseInt(parsedString[6]);
            int width = Integer.parseInt(parsedString[7]);

            if (mediaType.equals("InteractiveVideo")) {
                InteractiveVideoImpl interactiveVideo = new InteractiveVideoImpl(mediaType, width, height,
                        videoEncoding, bitrate, duration, producer);
//                interactiveVideo.setTags(Arrays.asList(tags));
                 mediaAdmin.upload(interactiveVideo);
            } else if (mediaType.equals("LicensedAudioVideo")) {
                String audioEncoding = parsedString[8];
                int samplingRate = Integer.parseInt(parsedString[9]);
                String holder = parsedString[10];
                LicensedAudioVideo licensedAudioVideo = new LicensedAudioVideoImpl(samplingRate, width, height,
                        audioEncoding, holder, bitrate, duration, producer);
                mediaAdmin.upload(licensedAudioVideo);
            } else {
                mediaView.displayError("Unsupported Media type");
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            mediaView.displayError("Invalid insert command");
        } catch (IllegalArgumentException e) {
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

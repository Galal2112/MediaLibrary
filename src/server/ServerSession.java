package server;

import businessLogic.MediaAdmin;
import mediaDB.*;
import model.InteractiveVideoImpl;
import model.LicensedAudioVideoImpl;
import model.Producer;
import mvc.Command;
import storage.InsufficientStorageException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

public abstract class ServerSession {

    final MediaAdmin mediaAdmin;
    private Command currentCommand = null;
    private final ArrayList<Command> commandList = new ArrayList<>(Arrays.asList(Command.CREATE, Command.DELETE,
            Command.VIEW));

    ServerSession(MediaAdmin mediaAdmin) {
        this.mediaAdmin = mediaAdmin;
    }

    void executeSession(DataInputStream in, DataOutputStream out) throws IOException {
        String command = in.readUTF();
        if (command.isEmpty()) {
            out.writeUTF("Not a valid input");
            return;
        }

        if (currentCommand == null || command.startsWith(":")) {
            Optional<Command> commandOptional = commandList.stream().filter(c -> c.getKey().equals(command)).findFirst();
            if (commandOptional.isPresent()) {
                currentCommand = commandOptional.get();
                out.writeUTF("Mode changed to " + command);
            } else {
                out.writeUTF("Please insert a valid command");
            }
        } else if (currentCommand == Command.CREATE) {
            handleCreateCommand(command, out);
        } else if (currentCommand == Command.VIEW) {
            if (command.equalsIgnoreCase("uploader")) {
                viewUploaders(out);
            } else if (command.toLowerCase().startsWith("content")) {
                viewContent(command, out);
            }
        } else  if (currentCommand == Command.DELETE) {
            try {
                mediaAdmin.deleteUploaderByName(command);
                out.writeUTF("Producer deleted successfully");
            } catch (IllegalArgumentException e) {
                try {
                    mediaAdmin.deleteMediaByAddress(command);
                    out.writeUTF("Media deleted successfully");
                } catch (IllegalArgumentException u) {
                    out.writeUTF("Invalid Input");
                }
            }
        }
    }

    private void handleCreateCommand(String command, DataOutputStream out) throws IOException {
        String[] parsedString = command.split(" ");

        if (parsedString.length == 1) {
            createProducer(command, out);
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
                out.writeUTF("Media uploaded successfully");
            } else if (isLicensedAudioVideo(mediaType)) {
                String audioEncoding = parsedString[8];
                int samplingRate = Integer.parseInt(parsedString[9]);
                String holder = parsedString[10];
                LicensedAudioVideo licensedAudioVideo = new LicensedAudioVideoImpl(samplingRate, width, height,
                        audioEncoding, holder, bitrate, duration, producer);
                licensedAudioVideo.setTags(tags);
                mediaAdmin.upload(licensedAudioVideo);
                out.writeUTF("Media uploaded successfully");
            } else {
                out.writeUTF("Unsupported Media type");
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            out.writeUTF("Invalid insert command");
        } catch (IllegalArgumentException | InsufficientStorageException e) {
            out.writeUTF(e.getMessage());
        }
    }

    private void createProducer(String text, DataOutputStream out) throws IOException {
        Producer producer = new Producer(text);
        try {
            mediaAdmin.createUploader(producer);
            out.writeUTF("Producer created successfully");
        } catch (IllegalArgumentException e) {
            out.writeUTF(e.getMessage());
        }
    }

    private void viewUploaders(DataOutputStream out) throws IOException {
        Map<Uploader, Integer> uploadersAndCounts = mediaAdmin.listProducersAndUploadsCount();
        if (uploadersAndCounts.size() == 0) {
            out.writeUTF("No registered uploader");
            return;
        }

        String response = "";
        for (Uploader uploader : uploadersAndCounts.keySet()) {
            response += uploader.getName() + ", " + uploadersAndCounts.get(uploader);
            response += "\n";
        }
        out.writeUTF(response);
    }

    private void viewContent(String command, DataOutputStream out) throws IOException {
        String[] split = command.split(" ");
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
                out.writeUTF("Unsupported Media type");
                return;
            }
        }

        String response = "";
        for (int i = 0; i < mediaList.size(); i++) {
            Object media = mediaList.get(i);
            if (media instanceof Audio) {
                Audio audio = (Audio) media;
                response += audio.getAddress() + ", " + audio.getUploadDate() + ", " + audio.getAccessCount();
            } else if (media instanceof Video) {
                Video video = (Video) media;
                response += video.getAddress() + ", " + video.getUploadDate() + ", " + video.getAccessCount();
            }
            response += "\n";
        }
        out.writeUTF(response);

    }

    private boolean isInteractiveVideo(String mediaType) {
        return mediaType.equals("InteractiveVideo");
    }

    private boolean isLicensedAudioVideo(String mediaType) {
        return mediaType.equals("LicensedAudioVideo");
    }
}

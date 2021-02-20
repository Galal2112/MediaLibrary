package server;

import businessLogic.MediaAdmin;
import mediaDB.*;
import model.Producer;
import mvc.Command;
import storage.InsufficientStorageException;
import util.MediaParser;
import util.MediaUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

public abstract class ServerSession {

    final MediaAdmin mediaAdmin;
    private Command currentCommand = null;
    private final ArrayList<Command> commandList = new ArrayList<>(Arrays.asList(Command.CREATE, Command.DELETE,
            Command.VIEW));

    ServerSession(MediaAdmin mediaAdmin) {
        this.mediaAdmin = mediaAdmin;
    }

    String executeSession(DataInputStream in) throws IOException {
        String command = in.readUTF();
        if (command.equals("exit")) {
            return null;
        }
        System.out.println("Command: " + command);
        if (command.isEmpty()) {
            return "Not a valid input";
        }

        if (currentCommand == null || command.startsWith(":")) {
            Optional<Command> commandOptional = commandList.stream().filter(c -> c.getKey().equals(command)).findFirst();
            if (commandOptional.isPresent()) {
                currentCommand = commandOptional.get();
                return "Mode changed to " + command;
            } else {
                return "Please insert a valid command";
            }
        } else if (currentCommand == Command.CREATE) {
            return handleCreateCommand(command);
        } else if (currentCommand == Command.VIEW) {
            if (command.equalsIgnoreCase("uploader")) {
                return viewUploaders();
            } else if (command.toLowerCase().startsWith("content")) {
                return viewContent(command);
            }
        } else if (currentCommand == Command.DELETE) {
            try {
                mediaAdmin.deleteUploaderByName(command);
                return "Producer deleted successfully";
            } catch (IllegalArgumentException e) {
                try {
                    mediaAdmin.deleteMediaByAddress(command);
                    return "Media deleted successfully";
                } catch (IllegalArgumentException u) {
                    return "Invalid Input";
                }
            }
        }
        return "Invalid command";
    }

    private String handleCreateCommand(String command) {
        String[] parsedString = command.split(" ");

        if (parsedString.length == 1) {
            return createProducer(command);
        }

        try {
            UploadableMediaContent mediaContent = MediaParser.parseMedia(command);
            mediaAdmin.upload(mediaContent);
            return "Media uploaded successfully";
        } catch (IllegalArgumentException | InsufficientStorageException e) {
            return e.getMessage();
        }
    }

    private String createProducer(String text) {
        Producer producer = new Producer(text);
        try {
            mediaAdmin.createUploader(producer);
            return "Producer created successfully";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    private String viewUploaders() {
        Map<Uploader, Integer> uploadersAndCounts = mediaAdmin.listProducersAndUploadsCount();
        if (uploadersAndCounts.size() == 0) {
            return "No registered uploader";
        }

        String response = "";
        for (Uploader uploader : uploadersAndCounts.keySet()) {
            response += uploader.getName() + ", " + uploadersAndCounts.get(uploader);
            response += "\n";
        }
        return response;
    }

    private String viewContent(String command) {
        String[] split = command.split(" ");
        List<?> mediaList;
        if (split.length == 1) {
            mediaList = mediaAdmin.listMedia(null);
        } else {
            String type = split[1];

            Class<? extends UploadableMediaContent> cls = MediaUtil.getMediaClass(type);
            if (cls != null) {
                mediaList = mediaAdmin.listMedia(cls);
            } else {
                return "Unsupported Media type";
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
        return response;
    }
}

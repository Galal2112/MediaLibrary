package util;

import mediaDB.Tag;
import mediaDB.UploadableMediaContent;
import model.*;

import java.util.ArrayList;
import java.util.List;

public final class MediaParser {
    private MediaParser() {}

    public static UploadableMediaContent parseMedia(String createCommand) throws IllegalArgumentException {
        try {
            String[] parsedString = createCommand.split(" ");
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
            long duration = Long.parseLong(parsedString[4]);

            if (isVideo(mediaType)) {
                String encoding = parsedString[5];
                int height = Integer.parseInt(parsedString[6]);
                int width = Integer.parseInt(parsedString[7]);

                if (mediaType.equals("Video") && parsedString.length == 8) {
                    return new VideoImpl(width, height, encoding, bitrate, duration, producer, tags);
                }

                if (isAudio(mediaType)) {
                    int samplingRate = Integer.parseInt(parsedString[9]);
                    if (mediaType.equals("AudioVideo") && parsedString.length == 10) {
                        return new AudioVideoImpl(width, height, samplingRate, encoding, bitrate, duration, producer, tags);
                    }
                    if (mediaType.equals("LicensedAudioVideo") && parsedString.length == 11) {
                        String holder = parsedString[10];
                        return new LicensedAudioVideoImpl(width, height, samplingRate, encoding, holder, bitrate, duration, producer, tags);
                    }
                    throw new IllegalArgumentException("Invalid Video type provided");
                }

                if (mediaType.equals("LicensedVideo") && parsedString.length == 9) {
                    String holder = parsedString[8];
                    return new LicensedVideoImpl(holder, width, height, encoding, bitrate, duration, producer, tags);
                }

                if (mediaType.equals("InteractiveVideo") && parsedString.length == 9) {
                    String type = parsedString[8];
                    return new InteractiveVideoImpl(type, width, height, encoding, bitrate, duration, producer, tags);
                }

                throw new IllegalArgumentException("Invalid Video type provided");
            }

            if (isAudio(mediaType)) {
                String encoding = parsedString[5];
                int samplingRate = Integer.parseInt(parsedString[6]);
                if (mediaType.equals("Audio") && parsedString.length == 7) {
                    return new AudioImpl(samplingRate, encoding, bitrate, duration, producer, tags);
                }
                if (mediaType.equals("LicensedAudio") && parsedString.length == 8) {
                    String holder = parsedString[7];
                    return new LicensedAudioImpl(holder, samplingRate, encoding, bitrate, duration, producer, tags);
                }
                throw new IllegalArgumentException("Invalid Audio type provided");
            }

            throw new IllegalArgumentException("Invalid Media type provided");
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Invalid creation command");
        }
    }

    private static boolean isVideo(String mediaType) {
        return mediaType.contains("Video");
    }

    private static boolean isAudio(String mediaType) {
        return mediaType.contains("Audio");
    }
}

package util;

import mediaDB.InteractiveVideo;
import mediaDB.LicensedAudioVideo;
import mediaDB.MediaContent;
import mediaDB.Uploadable;
import model.InteractiveVideoImpl;
import model.LicensedAudioVideoImpl;
import model.Producer;

import java.time.Duration;

public final class RandomGenerator {
    private RandomGenerator() {}

    private static final String[] availableEncodings = {"DWT", "DCT"};
    private static final Producer[] availableUploaders = {new Producer("Produzent1"), new Producer("Produzent2")
            , new Producer("Produzent3")};
    private static final String[] availableHolders = {"EdBangerRecords"};

    public static <T extends MediaContent & Uploadable> T getRandomMedia(Class<T> type) {
        // 480, 960, 1440, ......., 3840
        int width = getBoundedRandomNumber(8) * 480 ;
        // 270, 540, 810, 1080, ....., 2160
        int height = getBoundedRandomNumber(8) * 270;
        int encodingIndex = getBoundedRandomNumber(availableEncodings.length) - 1;
        int uploaderIndex = getBoundedRandomNumber(availableUploaders.length) - 1;
        // 300, 600, 900, ......, 9000
        long bitrate = getBoundedRandomNumber(30) * 300;
        // Video length in the range from 30 seconds to 30 minutes
        long lengthSeconds = getBoundedRandomNumber(60) * 30;

        if (type == InteractiveVideo.class) {
            return (T) new InteractiveVideoImpl("Interactive", width, height,
                    availableEncodings[encodingIndex], bitrate, Duration.ofSeconds(lengthSeconds),
                    availableUploaders[uploaderIndex]);
        } else if (type == LicensedAudioVideo.class) {
            int samplingRate = getBoundedRandomNumber(8000) * 8;
            int holderIndex = getBoundedRandomNumber(availableHolders.length) - 1;
            return (T) new LicensedAudioVideoImpl(samplingRate, width, height, availableEncodings[encodingIndex],
                    availableHolders[holderIndex], bitrate, Duration.ofSeconds(lengthSeconds),
                    availableUploaders[uploaderIndex]);
        } else {
            return null;
        }
    }

    public static int getBoundedRandomNumber(int bound) {
        return (int) (bound * Math.random()) + 1;
    }

}

package util;

import mediaDB.Video;
import model.InteractiveVideoImpl;
import model.LicensedAudioVideoImpl;
import model.Producer;

import java.util.ArrayList;

public final class RandomGenerator {
    private RandomGenerator() {}

    private static final String[] availableEncodings = {"DWT", "DCT"};
    private static final Producer[] availableUploaders = {new Producer("Produzent1"), new Producer("Produzent2")
            , new Producer("Produzent3")};
    private static final String[] availableHolders = {"EdBangerRecords"};

    public static Video getRandomMedia() {
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

        int random = RandomGenerator.getBoundedRandomNumber(2);

        if (random == 1) {
            return new InteractiveVideoImpl("Interactive", width, height,
                    availableEncodings[encodingIndex], bitrate, lengthSeconds,
                    availableUploaders[uploaderIndex], new ArrayList<>());
        } else {
            int samplingRate = getBoundedRandomNumber(8000) * 8;
            int holderIndex = getBoundedRandomNumber(availableHolders.length) - 1;
            return new LicensedAudioVideoImpl(width, height, samplingRate, availableEncodings[encodingIndex],
                    availableHolders[holderIndex], bitrate, lengthSeconds,
                    availableUploaders[uploaderIndex], new ArrayList<>());
        }
    }

    public static int getBoundedRandomNumber(int bound) {
        return (int) (bound * Math.random()) + 1;
    }

}

package util;

import mediaDB.*;
import model.*;

public final class MediaUtil {
    private MediaUtil() {}

    public static Class<? extends UploadableMediaContent> getMediaClass(String type) {
        switch (type) {
            case "Audio":
                return AudioImpl.class;
            case "AudioVideo":
                return AudioVideoImpl.class;
            case "InteractiveVideo":
                return InteractiveVideoImpl.class;
            case "LicensedAudio":
                return LicensedAudioImpl.class;
            case "LicensedAudioVideo":
                return LicensedAudioVideoImpl.class;
            case "LicensedVideo":
                return LicensedVideoImpl.class;
            case "Video":
                return VideoImpl.class;
            default:
                return null;
        }
    }
}

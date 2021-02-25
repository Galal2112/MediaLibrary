import model.*;
import org.junit.jupiter.api.Test;
import util.MediaUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MediaUtilTest {

    @Test
    void testReturnCorrectClass() {
        assertEquals(MediaUtil.getMediaClass("Audio"), AudioImpl.class);
        assertEquals(MediaUtil.getMediaClass("AudioVideo"), AudioVideoImpl.class);
        assertEquals(MediaUtil.getMediaClass("InteractiveVideo"), InteractiveVideoImpl.class);
        assertEquals(MediaUtil.getMediaClass("LicensedAudio"), LicensedAudioImpl.class);
        assertEquals(MediaUtil.getMediaClass("LicensedAudioVideo"), LicensedAudioVideoImpl.class);
        assertEquals(MediaUtil.getMediaClass("LicensedVideo"), LicensedVideoImpl.class);
        assertEquals(MediaUtil.getMediaClass("Video"), VideoImpl.class);
    }
}

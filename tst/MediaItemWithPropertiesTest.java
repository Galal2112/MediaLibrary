import gui.MediaItemWithProperties;
import mediaDB.InteractiveVideo;
import mediaDB.Uploader;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class MediaItemWithPropertiesTest {

    @Test
    void testCorrectStateForUploadableMedia() {
        Uploader uploader = Mockito.mock(Uploader.class);
        when(uploader.getName()).thenReturn("TestUploader");
        InteractiveVideo video = Mockito.mock(InteractiveVideo.class);
        when(video.getAddress()).thenReturn("1@InteractiveVideo");
        when(video.getUploadDate()).thenReturn(new Date());
        when(video.getAccessCount()).thenReturn(1L);
        when(video.getUploader()).thenReturn(uploader);
        MediaItemWithProperties mediaItemWithProperties = new MediaItemWithProperties(video);
        assertEquals(mediaItemWithProperties.getProducer(), uploader.getName());
        assertEquals(mediaItemWithProperties.getType(), video.getClass().getSimpleName());
        assertEquals(mediaItemWithProperties.getAddress(), video.getAddress());
        assertEquals(mediaItemWithProperties.getDate(), video.getUploadDate().toString());
    }

    @Test
    void testUndefinedStateForNonAudioVideo() {
        MediaItemWithProperties mediaItemWithProperties = new MediaItemWithProperties(new Object());
        assertEquals(mediaItemWithProperties.getProducer(), "undefined");
        assertEquals(mediaItemWithProperties.getType(), "undefined");
        assertEquals(mediaItemWithProperties.getAddress(), "undefined");
        assertEquals(mediaItemWithProperties.getDate(), "undefined");
    }
}

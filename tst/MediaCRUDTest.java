import crud.MediaCRUD;
import mediaDB.InteractiveVideo;
import mediaDB.MediaContent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MediaCRUDTest {

    private final MediaCRUD mediaCRUD = new MediaCRUD();

    @Test
    void getAll() {
        InteractiveVideo video = getMockVideo();
        mediaCRUD.create(video);
        List<MediaContent> mediaList = mediaCRUD.getAll();
        assertNotNull(mediaList);
        assertTrue(mediaList.size() > 0);
    }

    @Test
    void create() {
        List<MediaContent> originalMediaList = mediaCRUD.getAll();
        InteractiveVideo video = getMockVideo();
        mediaCRUD.create(video);
        List<MediaContent> updatedMediaList = mediaCRUD.getAll();
        assertEquals(originalMediaList.size() + 1, updatedMediaList.size());
    }

    @Test
    void update() {
        String testAddress = "InteractiveVideo@TestUpdate1234";
        long expectedAccessCount = 1;

        // 1 create video
        InteractiveVideo video = getMockVideo();
        when(video.getAddress()).thenReturn(testAddress);
        mediaCRUD.create(video);

        // 2 updated video
        InteractiveVideo updatedVideo = getMockVideo();
        when(updatedVideo.getAddress()).thenReturn(testAddress);
        when(updatedVideo.getAccessCount()).thenReturn(expectedAccessCount);
        mediaCRUD.update(updatedVideo);

        List<MediaContent> mediaList = mediaCRUD.getAll();
        Optional<MediaContent> videoOptional = mediaList.stream().filter(v -> v.getAddress().equals(testAddress)).findFirst();
        assertTrue(videoOptional.isPresent());
        InteractiveVideo testVideo = (InteractiveVideo) videoOptional.get();
        assertEquals(testVideo.getAccessCount(), expectedAccessCount);
    }

    @Test
    void delete() {
        InteractiveVideo testVideo = getMockVideo();
        mediaCRUD.create(testVideo);
        List<MediaContent> originalMediaList = mediaCRUD.getAll();
        mediaCRUD.delete(testVideo);
        List<MediaContent> newMediaList = mediaCRUD.getAll();
        assertEquals(originalMediaList.size() - 1, newMediaList.size());
    }

    @Test
    void testDelete_usingAddress() {
        InteractiveVideo testVideo = getMockVideo();
        mediaCRUD.create(testVideo);
        List<MediaContent> originalMediaList = mediaCRUD.getAll();
        mediaCRUD.deleteById(testVideo.getAddress());
        List<MediaContent> newMediaList = mediaCRUD.getAll();
        assertEquals(originalMediaList.size() - 1, newMediaList.size());
    }

    private InteractiveVideo getMockVideo() {
        InteractiveVideo video = Mockito.mock(InteractiveVideo.class);
        when(video.getAddress()).thenReturn("InteractiveVideo@" + System.currentTimeMillis());
        when(video.getUploadDate()).thenReturn(new Date());
        when(video.copy()).thenReturn(video);
        return video;
    }
}
package crud;

import mediaDB.InteractiveVideo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class InteractiveVideoCRUDTest {

    private InteractiveVideoCRUD interactiveVideoCRUD = new InteractiveVideoCRUD();;

    @Test
    void getAll() {
        InteractiveVideo video = getMockVideo();
        interactiveVideoCRUD.create(video);
        List<InteractiveVideo> videos = interactiveVideoCRUD.getAll();
        assertNotNull(videos);
        assertTrue(videos.size() > 0);
    }

    @Test
    void create() {
        List<InteractiveVideo> originalVideos = interactiveVideoCRUD.getAll();
        InteractiveVideo video = getMockVideo();
        interactiveVideoCRUD.create(video);
        List<InteractiveVideo> updatedVideos = interactiveVideoCRUD.getAll();
        assertEquals(originalVideos.size() + 1, updatedVideos.size());
    }

    @Test
    void update() {
        String testAddress = "InteractiveVideo@TestUpdate1234";
        long expectedAccessCount = 1;

        // 1 create video
        InteractiveVideo video = getMockVideo();
        when(video.getAddress()).thenReturn(testAddress);
        interactiveVideoCRUD.create(video);

        // 2 updated video
        InteractiveVideo updatedVideo = getMockVideo();
        when(updatedVideo.getAddress()).thenReturn(testAddress);
        when(updatedVideo.getAccessCount()).thenReturn(expectedAccessCount);
        interactiveVideoCRUD.update(updatedVideo);

        List<InteractiveVideo> videos = interactiveVideoCRUD.getAll();
        Optional<InteractiveVideo> videoOptional = videos.stream().filter(v -> v.getAddress().equals(testAddress)).findFirst();
        assertTrue(videoOptional.isPresent());
        InteractiveVideo testVideo = videoOptional.get();
        assertEquals(testVideo.getAccessCount(), expectedAccessCount);
    }

    @Test
    void delete() {
        InteractiveVideo testVideo = getMockVideo();
        interactiveVideoCRUD.create(testVideo);
        List<InteractiveVideo> originalVideos = interactiveVideoCRUD.getAll();
        interactiveVideoCRUD.delete(testVideo);
        List<InteractiveVideo> newVideosList = interactiveVideoCRUD.getAll();
        assertEquals(originalVideos.size() - 1, newVideosList.size());
    }

    @Test
    void testDelete_usingAddress() {
        InteractiveVideo testVideo = getMockVideo();
        interactiveVideoCRUD.create(testVideo);
        List<InteractiveVideo> originalVideos = interactiveVideoCRUD.getAll();
        interactiveVideoCRUD.deleteById(testVideo.getAddress());
        List<InteractiveVideo> newVideosList = interactiveVideoCRUD.getAll();
        assertEquals(originalVideos.size() - 1, newVideosList.size());
    }

    private InteractiveVideo getMockVideo() {
        InteractiveVideo video = Mockito.mock(InteractiveVideo.class);
        when(video.getAddress()).thenReturn("InteractiveVideo@" + System.currentTimeMillis());
        when(video.getUploadDate()).thenReturn(new Date());
        return video;
    }
}
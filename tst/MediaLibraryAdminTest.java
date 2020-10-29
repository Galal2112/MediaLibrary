import businessLogic.MediaAdmin;
import businessLogic.MediaLibraryAdmin;
import crud.InteractiveVideoCRUD;
import crud.LicensedAudioVideoCRUD;
import crud.UploaderCRUD;
import mediaDB.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MediaLibraryAdminTest {

    private MediaAdmin mediaAdmin;
    private InteractiveVideoCRUD interactiveVideoCRUD;
    private LicensedAudioVideoCRUD licensedAudioVideoCRUD;
    private UploaderCRUD uploaderCRUD;

    @BeforeEach
    void setUp() {
        interactiveVideoCRUD = Mockito.mock(InteractiveVideoCRUD.class);
        licensedAudioVideoCRUD = Mockito.mock(LicensedAudioVideoCRUD.class);
        uploaderCRUD = Mockito.mock(UploaderCRUD.class);
        mediaAdmin = new MediaLibraryAdmin(uploaderCRUD, interactiveVideoCRUD, licensedAudioVideoCRUD);
    }

    @Test
    void createUploader() {
        String uploaderName = "TestUploader";
        // Test create uploader which does not exist
        Uploader uploader = Mockito.mock(Uploader.class);
        when(uploader.getName()).thenReturn(uploaderName);
        when(uploaderCRUD.get(uploaderName)).thenReturn(Optional.empty());
        mediaAdmin.createUploader(uploader);
        // verify uploaderCRUD methods called
        verify(uploaderCRUD).get(uploaderName);
        verify(uploaderCRUD).create(uploader);

        // Test create uploader which exists
        when(uploaderCRUD.get(uploaderName)).thenReturn(Optional.of(uploader));
        assertThrows(IllegalArgumentException.class, () -> mediaAdmin.createUploader(uploader));
    }

    @Test
    void upload() {
        // Verify only InteractiveVideo and LicensedAudioVideo supported
        LicensedVideo licensedVideo = mock(LicensedVideo.class);
        assertThrows(IllegalArgumentException.class, () -> mediaAdmin.upload(licensedVideo));

        String uploaderName = "TestUploader";
        Uploader uploader = Mockito.mock(Uploader.class);
        when(uploader.getName()).thenReturn(uploaderName);
        LicensedAudioVideo licensedAudioVideo = mock(LicensedAudioVideo.class);
        when(licensedAudioVideo.getUploader()).thenReturn(uploader);

        // Test uploader doesn't exist
        when(uploaderCRUD.get(uploaderName)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> mediaAdmin.upload(licensedAudioVideo));

        // Test insufficient storage
        when(licensedAudioVideo.getSize()).thenReturn(new BigDecimal(MediaLibraryAdmin.availableStorageTB.incrementAndGet()));
        assertThrows(IllegalArgumentException.class, () -> mediaAdmin.upload(licensedAudioVideo));

        // Test upload
        when(licensedAudioVideo.getSize()).thenReturn(new BigDecimal(4 * 1024));
        when(uploaderCRUD.get(uploaderName)).thenReturn(Optional.of(uploader));
        mediaAdmin.upload(licensedAudioVideo);
        // verify set address and upload date
        verify(licensedAudioVideo).setAddress(any());
        verify(licensedAudioVideo).setUploadDate(any());
        verify(licensedAudioVideoCRUD).create(licensedAudioVideo);
    }

    @Test
    void listProducersAndUploadsCount() {
        int producersCount = 5;

        Map<String, Integer> expectedCounts = new HashMap<>();
        List<Uploader> uploaders = new ArrayList<>();
        List<InteractiveVideo> interactiveVideos = new ArrayList<>();
        List<LicensedAudioVideo> audioVideos = new ArrayList<>();
        // mock producers and videos
        for (int i = 0; i < producersCount; i ++) {
            String name = "uploader " + i;
            Uploader uploader = Mockito.mock(Uploader.class);
            when(uploader.getName()).thenReturn(name);
            uploaders.add(uploader);
            if (i == 0) {
                // set first uploader videos count to zero
                expectedCounts.put(name, 0);
                continue;
            }
            InteractiveVideo interactiveVideo = mock(InteractiveVideo.class);
            when(interactiveVideo.getUploader()).thenReturn(uploader);
            interactiveVideos.add(interactiveVideo);
            if (i % 2 == 0) {
                LicensedAudioVideo licensedAudioVideo = mock(LicensedAudioVideo.class);
                when(licensedAudioVideo.getUploader()).thenReturn(uploader);
                audioVideos.add(licensedAudioVideo);
                expectedCounts.put(name, 2);
            } else {
                expectedCounts.put(name, 1);
            }
        }
        when(uploaderCRUD.getAll()).thenReturn(uploaders);
        when(interactiveVideoCRUD.getAll()).thenReturn(interactiveVideos);
        when(licensedAudioVideoCRUD.getAll()).thenReturn(audioVideos);

        // Test get uploader and counts
        Map<Uploader, Integer> uploaderCounts = mediaAdmin.listProducersAndUploadsCount();
        verify(uploaderCRUD).getAll();
        verify(interactiveVideoCRUD).getAll();
        verify(licensedAudioVideoCRUD).getAll();

        Set<Uploader> resultUploaders = uploaderCounts.keySet();
        // check result has all the producers
        assertEquals(resultUploaders.size(), producersCount);
        // check uploads count
        for (Uploader uploader : resultUploaders) {
            assertEquals(uploaderCounts.get(uploader), expectedCounts.get(uploader.getName()));
        }
    }

    @Test
    void listMedia() {
        List<InteractiveVideo> interactiveVideos = new ArrayList<>();
        interactiveVideos.add(mock(InteractiveVideo.class));
        when(interactiveVideoCRUD.getAll()).thenReturn(interactiveVideos);

        List<LicensedAudioVideo> audioVideos = new ArrayList<>();
        audioVideos.add(mock(LicensedAudioVideo.class));
        audioVideos.add(mock(LicensedAudioVideo.class));
        when(licensedAudioVideoCRUD.getAll()).thenReturn(audioVideos);

        // Test result size
        assertEquals(mediaAdmin.listMedia(InteractiveVideo.class).size(), interactiveVideos.size());
        verify(interactiveVideos.get(0)).setAccessCount(1);
        verify(interactiveVideoCRUD).update(interactiveVideos.get(0));

        assertEquals(mediaAdmin.listMedia(LicensedAudioVideo.class).size(), audioVideos.size());
        verify(audioVideos.get(0)).setAccessCount(1);
        verify(licensedAudioVideoCRUD).update(audioVideos.get(0));

        assertEquals(mediaAdmin.listMedia(null).size(), interactiveVideos.size() + audioVideos.size());

    }

    @Test
    void getAllTags() {
        Tag[] expectedTags = Tag.values();
        List<Tag> tags = mediaAdmin.getAllTags();
        assertEquals(expectedTags.length, tags.size());
        for (int i = 0; i < expectedTags.length; i ++) {
            assertEquals(expectedTags[i], tags.get(i));
        }
    }

    @Test
    void deleteUploaderByName() {
        String uploaderName = "DeletedUploader";
        mediaAdmin.deleteUploaderByName(uploaderName);
        verify(uploaderCRUD).deleteById(uploaderName);
    }

    @Test
    void deleteUploader() {
        String uploaderName = "DeletedUploader";
        Uploader uploader = Mockito.mock(Uploader.class);
        when(uploader.getName()).thenReturn(uploaderName);

        mediaAdmin.deleteUploader(uploader);
        verify(uploaderCRUD).delete(uploader);
    }

    @Test
    void deleteMedia() {
        InteractiveVideo interactiveVideo = mock(InteractiveVideo.class);
        mediaAdmin.deleteMedia(interactiveVideo);
        verify(interactiveVideoCRUD).delete(interactiveVideo);

        LicensedAudioVideo licensedAudioVideo = mock(LicensedAudioVideo.class);
        mediaAdmin.deleteMedia(licensedAudioVideo);
        verify(licensedAudioVideoCRUD).delete(licensedAudioVideo);
    }

    @Test
    void deleteMediaByAddress() {
        String deletedAddress = "test address";
        mediaAdmin.deleteMediaByAddress(deletedAddress);
        verify(interactiveVideoCRUD).deleteById(deletedAddress);
        verify(licensedAudioVideoCRUD).deleteById(deletedAddress);
    }
}
package businessLogic;

import crud.InteractiveVideoCRUD;
import crud.LicensedAudioVideoCRUD;
import crud.UploaderCRUD;
import mediaDB.LicensedAudioVideo;
import mediaDB.LicensedVideo;
import mediaDB.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MediaLibraryAdminTest {

    MediaLibraryAdmin mediaAdmin;
    InteractiveVideoCRUD interactiveVideoCRUD;
    LicensedAudioVideoCRUD licensedAudioVideoCRUD;
    UploaderCRUD uploaderCRUD;

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
        when(licensedAudioVideo.getSize()).thenReturn(MediaLibraryAdmin.availableStorage.add(new BigDecimal(1)));
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
    }

    @Test
    void listMedia() {
    }

    @Test
    void getAllTags() {
    }

    @Test
    void deleteUploaderByName() {
    }

    @Test
    void deleteUploader() {
    }

    @Test
    void testDeleteUploader() {
    }

    @Test
    void deleteMedia() {
    }
}
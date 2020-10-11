package businessLogic;

import crud.InteractiveVideoCRUD;
import crud.LicensedAudioVideoCRUD;
import crud.UploaderCRUD;
import mediaDB.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        // Test create uploader which doesnot exist
        Uploader uploader = Mockito.mock(Uploader.class);
        when(uploader.getName()).thenReturn(uploaderName);
        when(uploaderCRUD.get(uploaderName)).thenReturn(Optional.empty());
        mediaAdmin.createUploader(uploader);
        verify(uploaderCRUD).get(uploaderName);
        verify(uploaderCRUD).create(uploader);

        // Test create uploader which exists
        when(uploaderCRUD.get(uploaderName)).thenReturn(Optional.of(uploader));
        assertThrows(IllegalArgumentException.class, () -> mediaAdmin.createUploader(uploader));
    }

    @Test
    void upload() {
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
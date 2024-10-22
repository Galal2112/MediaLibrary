import businessLogic.MediaAdmin;
import businessLogic.MediaLibraryAdmin;
import crud.MediaCRUD;
import crud.UploaderCRUD;
import mediaDB.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import persistence.PersistenceHelper;
import storage.InsufficientStorageException;
import storage.MediaStorage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MediaLibraryAdminTest {

    private MediaStorage mediaStorage;
    private MediaAdmin mediaAdmin;
    private MediaCRUD mediaCRUD;
    private UploaderCRUD uploaderCRUD;
    private PersistenceHelper persistenceHelper;

    @BeforeEach
    void setUp() {
        mediaStorage = new MediaStorage(10 * 1024);
        mediaCRUD = Mockito.mock(MediaCRUD.class);
        uploaderCRUD = Mockito.mock(UploaderCRUD.class);
        persistenceHelper = Mockito.mock(PersistenceHelper.class);
        mediaAdmin = new MediaLibraryAdmin(persistenceHelper, mediaStorage, uploaderCRUD, mediaCRUD);
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
        String uploaderName = "TestUploader";
        Uploader uploader = Mockito.mock(Uploader.class);
        when(uploader.getName()).thenReturn(uploaderName);
        LicensedAudioVideo licensedAudioVideo = mock(LicensedAudioVideo.class);
        when(licensedAudioVideo.getUploader()).thenReturn(uploader);

        // Test uploader doesn't exist
        when(uploaderCRUD.get(uploaderName)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> mediaAdmin.upload(licensedAudioVideo));

        // Test insufficient storage
        BigDecimal storage = mediaStorage.getAvailableMediaStorageInMB();
        when(licensedAudioVideo.getSize()).thenReturn(storage.add(new BigDecimal(1)));
        assertThrows(IllegalArgumentException.class, () -> mediaAdmin.upload(licensedAudioVideo));

        // Test upload
        when(licensedAudioVideo.getSize()).thenReturn(new BigDecimal(4 * 1024));
        when(uploaderCRUD.get(uploaderName)).thenReturn(Optional.of(uploader));
        try {
            mediaAdmin.upload(licensedAudioVideo);
        } catch (InsufficientStorageException e) {
            fail(e.getMessage());
        }
        // verify set address and upload date
        verify(licensedAudioVideo).setAddress(any());
        verify(licensedAudioVideo).setUploadDate(any());
        verify(mediaCRUD).create(licensedAudioVideo);
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
        List<MediaContent> allMedia = new LinkedList<>();
        allMedia.addAll(interactiveVideos);
        allMedia.addAll(audioVideos);
        when(mediaCRUD.getAll()).thenReturn(allMedia);

        // Test get uploader and counts
        Map<Uploader, Integer> uploaderCounts = mediaAdmin.listProducersAndUploadsCount();
        verify(uploaderCRUD).getAll();
        verify(mediaCRUD).getAll();

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

        List<LicensedAudioVideo> audioVideos = new ArrayList<>();
        audioVideos.add(mock(LicensedAudioVideo.class));
        audioVideos.add(mock(LicensedAudioVideo.class));

        List<MediaContent> allMedia = new LinkedList<>();
        allMedia.addAll(interactiveVideos);
        allMedia.addAll(audioVideos);
        when(mediaCRUD.getAll()).thenReturn(allMedia);

        // Test result size
        assertEquals(mediaAdmin.listMedia(mock(InteractiveVideo.class).getClass()).size(), interactiveVideos.size());
        assertEquals(mediaAdmin.listMedia(mock(LicensedAudioVideo.class).getClass()).size(), audioVideos.size());
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
    void getUsedTags() {
        InteractiveVideo testedVideo = mock(InteractiveVideo.class);
        when(testedVideo.getTags()).thenReturn(Arrays.asList(Tag.Lifestyle, Tag.News));
        when(mediaCRUD.getAll()).thenReturn(Arrays.asList(testedVideo));
        List<Tag> usedTags = mediaAdmin.getUsedTags();
        assertEquals(usedTags.size(), testedVideo.getTags().size());
        for (Tag tag : testedVideo.getTags()) {
            assertTrue(usedTags.contains(tag));
        }
    }

    @Test
    void getUnusedTags() {
        InteractiveVideo testedVideo = mock(InteractiveVideo.class);
        when(testedVideo.getTags()).thenReturn(Arrays.asList(Tag.Lifestyle, Tag.News));
        when(mediaCRUD.getAll()).thenReturn(Arrays.asList(testedVideo));
        List<Tag> unusedTags = mediaAdmin.getUnusedTags();
        assertEquals(unusedTags.size(), Tag.values().length - testedVideo.getTags().size());
        for (Tag tag : testedVideo.getTags()) {
            assertFalse(unusedTags.contains(tag));
        }
    }

    @Test
    void deleteUploaderByName() {
        String uploaderName = "DeletedUploader";
        Uploader uploader = Mockito.mock(Uploader.class);
        when(uploaderCRUD.get(uploaderName)).thenReturn(Optional.of(uploader));
        mediaAdmin.createUploader(uploader);
        mediaAdmin.deleteUploaderByName(uploaderName);
        verify(uploaderCRUD).deleteById(uploaderName);
    }

    @Test
    void deleteUploader() {
        String uploaderName = "DeletedUploader";
        Uploader uploader = Mockito.mock(Uploader.class);
        when(uploader.getName()).thenReturn(uploaderName);
        when(uploaderCRUD.get(uploaderName)).thenReturn(Optional.of(uploader));

        mediaAdmin.deleteUploader(uploader);
        verify(uploaderCRUD).deleteById(uploader.getName());
    }

    @Test
    void deleteMedia() {
        String interactiveVideoAddress = "interactiveVideoAddress";
        InteractiveVideo interactiveVideo = mock(InteractiveVideo.class);
        when(interactiveVideo.getAddress()).thenReturn(interactiveVideoAddress);
        when(interactiveVideo.getSize()).thenReturn(new BigDecimal(10));
        when(mediaCRUD.get(interactiveVideoAddress)).thenReturn(Optional.of(interactiveVideo));
        mediaAdmin.deleteMedia(interactiveVideo);
        verify(mediaCRUD).deleteById(interactiveVideo.getAddress());

        String licensedAudioVideoAddress = "licensedAudioVideoAddress";
        LicensedAudioVideo licensedAudioVideo = mock(LicensedAudioVideo.class);
        when(licensedAudioVideo.getAddress()).thenReturn(licensedAudioVideoAddress);
        when(mediaCRUD.get(licensedAudioVideoAddress)).thenReturn(Optional.of(licensedAudioVideo));
        when(licensedAudioVideo.getSize()).thenReturn(new BigDecimal(10));
        mediaAdmin.deleteMedia(licensedAudioVideo);
        verify(mediaCRUD).deleteById(licensedAudioVideo.getAddress());
    }

    @Test
    void deleteMediaByAddress() {
        String deletedAddress = "test address";

        Uploader uploader = Mockito.mock(Uploader.class);
        when(uploaderCRUD.get(any())).thenReturn(Optional.of(uploader));
        LicensedAudioVideo licensedAudioVideo = mock(LicensedAudioVideo.class);
        when(mediaCRUD.get(deletedAddress)).thenReturn(Optional.of(licensedAudioVideo));
        when(licensedAudioVideo.getSize()).thenReturn(new BigDecimal(10));
        when(licensedAudioVideo.getAddress()).thenReturn(deletedAddress);
        when(licensedAudioVideo.getUploader()).thenReturn(uploader);
        try {
            mediaAdmin.upload(licensedAudioVideo);
        } catch (InsufficientStorageException e) {
            fail(e.getMessage());
        }

        mediaAdmin.deleteMediaByAddress(deletedAddress);
        verify(mediaCRUD).deleteById(deletedAddress);
    }

    @Test
    void getUploader() {
        String uploaderName = "TestUploader";
        Uploader uploader = Mockito.mock(Uploader.class);
        when(uploader.getName()).thenReturn(uploaderName);
        when(uploaderCRUD.get(uploaderName)).thenReturn(Optional.of(uploader));
        Optional<Uploader> resultUploader = mediaAdmin.getUploader(uploaderName);
        assertFalse(resultUploader.isEmpty());
        assertEquals(resultUploader.get().getName(), uploaderName);
        verify(uploaderCRUD).get(uploaderName);
    }

    @Test
    void retrieveMediaByAddress() {
        String testAddress = "test address";
        InteractiveVideo interactiveVideo = mock(InteractiveVideo.class);
        when(interactiveVideo.getAddress()).thenReturn(testAddress);
        when(mediaCRUD.get(testAddress)).thenReturn(Optional.of(interactiveVideo));
        Optional<MediaContent> result = mediaAdmin.retrieveMediaByAddress(testAddress);
        assertFalse(result.isEmpty());
        assertEquals(result.get().getAddress(), testAddress);
        verify(interactiveVideo).setAccessCount(1);
        verify(mediaCRUD).get(testAddress);
    }

    @Test
    void updateMedia() {
        InteractiveVideo interactiveVideo = mock(InteractiveVideo.class);
        mediaAdmin.update(interactiveVideo);
        verify(mediaCRUD).update(interactiveVideo);
    }

    @Test
    void saveJOS() {
        try {
            when(mediaCRUD.getAll()).thenReturn(new ArrayList<>());
            when(uploaderCRUD.getAll()).thenReturn(new ArrayList<>());
            mediaAdmin.saveJOS();
            verify(persistenceHelper, atLeastOnce()).saveJOS(anyList(), anyString());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void loadJOS() {
        try {
            mediaAdmin.loadJOS();
            verify(persistenceHelper, atLeastOnce()).loadJOS(anyString());
        } catch (IOException | InsufficientStorageException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void saveJBP() {
        try {
            mediaAdmin.saveJBP();
            verify(persistenceHelper).saveMediaUsingJBP(anyString(), anyList());
            verify(persistenceHelper).saveUploadersUsingJBP(anyString(), anyList());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void loadJBP() {
        try {
            mediaAdmin.loadJBP();
            verify(persistenceHelper).loadMediaUsingJBP(anyString());
            verify(persistenceHelper).loadUploaderUsingJBP(anyString());
        } catch (IOException | ClassNotFoundException | InsufficientStorageException exception) {
            exception.printStackTrace();
        }
    }
}
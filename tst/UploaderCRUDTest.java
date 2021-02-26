import crud.UploaderCRUD;
import mediaDB.Uploader;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UploaderCRUDTest {
    private final UploaderCRUD uploaderCRUD = new UploaderCRUD();

    @Test
    void getAll() {
        Uploader uploader = getMockUploader();
        uploaderCRUD.create(uploader);
        List<Uploader> uploaders = uploaderCRUD.getAll();
        assertNotNull(uploaders);
        assertTrue(uploaders.size() > 0);
    }

    @Test
    void create() {
        List<Uploader> originalUploaders = uploaderCRUD.getAll();
        Uploader uploader = getMockUploader();
        uploaderCRUD.create(uploader);
        List<Uploader> uploaders = uploaderCRUD.getAll();
        assertEquals(originalUploaders.size() + 1, uploaders.size());
    }

    @Test
    void delete() {
        Uploader uploader = getMockUploader();
        uploaderCRUD.create(uploader);
        List<Uploader> originalUploaders = uploaderCRUD.getAll();
        uploaderCRUD.delete(uploader);
        List<Uploader> newUploaders = uploaderCRUD.getAll();
        assertEquals(originalUploaders.size() - 1, newUploaders.size());
    }

    private Uploader getMockUploader() {
        Uploader uploader = Mockito.mock(Uploader.class);
        when(uploader.getName()).thenReturn("Test Uploader" + System.currentTimeMillis());
        when(uploader.copy()).thenReturn(uploader);
        return uploader;
    }
}

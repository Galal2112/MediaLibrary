import mediaDB.MediaContent;
import persistence.PersistenceManager;
import model.InteractiveVideoImpl;
import model.Producer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class SaveLoadTest {

    @Test
    void save() throws IOException {
        String address = "0@TestAddress";
        RandomAccessFile indexRas = mock(RandomAccessFile.class);
        when(indexRas.getFilePointer()).thenReturn(0l);
        RandomAccessFile mediaRas = mock(RandomAccessFile.class);
        InteractiveVideoImpl interactiveVideo = new InteractiveVideoImpl("Interactive", 100, 200,  "DWT", 300, 3600, new Producer("Test"), new ArrayList<>());
        interactiveVideo.setAddress(address);
        interactiveVideo.setUploadDate(new Date());
        try {
            PersistenceManager.saveRandom(interactiveVideo, indexRas, mediaRas);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        try {
            verify(mediaRas, atLeastOnce()).seek(anyLong());
            verify(mediaRas, atLeastOnce()).writeUTF("Interactive");
            verify(mediaRas, atLeastOnce()).writeInt(100);
            verify(mediaRas, atLeastOnce()).writeLong(300);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void load() throws IOException {
        RandomAccessFile indexRas = mock(RandomAccessFile.class);
        when(indexRas.getFilePointer()).thenReturn(0l);
        RandomAccessFile mediaRas = mock(RandomAccessFile.class);

        try {
            MediaContent mediaContent = PersistenceManager.loadRandom("0@TestAddress", indexRas, mediaRas);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        try {
            verify(mediaRas, atLeastOnce()).readUTF();
            verify(mediaRas, atLeastOnce()).readInt();
            verify(mediaRas, atLeastOnce()).readLong();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}

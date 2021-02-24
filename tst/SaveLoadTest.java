import model.InteractiveVideoImpl;
import model.Producer;
import org.junit.jupiter.api.Test;
import persistence.PersistenceManager;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class SaveLoadTest {

    @Test
    void save() {
        long seek = 10;
        RandomAccessFile randomAccessFile = mock(RandomAccessFile.class);
        InteractiveVideoImpl interactiveVideo = new InteractiveVideoImpl("Interactive", 100, 200,  "DWT", 300, 3600, new Producer("Test"), new ArrayList<>());
        interactiveVideo.setAddress("Address");
        interactiveVideo.setUploadDate(new Date());
        try {
            PersistenceManager.saveInteractiveVideo(randomAccessFile, seek, interactiveVideo);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        try {
            verify(randomAccessFile, atLeastOnce()).seek(seek);
            verify(randomAccessFile, atLeastOnce()).writeUTF("Interactive");
            verify(randomAccessFile, atLeastOnce()).writeInt(100);
            verify(randomAccessFile, atLeastOnce()).writeLong(300);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void load() {
        RandomAccessFile randomAccessFile = mock(RandomAccessFile.class);
        try {
            InteractiveVideoImpl interactiveVideo = PersistenceManager.loadInteractiveVideo(randomAccessFile);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        try {
            verify(randomAccessFile, atLeastOnce()).readUTF();
            verify(randomAccessFile, atLeastOnce()).readInt();
            verify(randomAccessFile, atLeastOnce()).readLong();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}

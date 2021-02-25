import model.InteractiveVideoImpl;
import model.LicensedAudioImpl;
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
    void saveInteractiveVideo() {
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
    void loadInteractiveVideo() {
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

    @Test
    void saveLicensedAudio() {
        long seek = 10;
        RandomAccessFile randomAccessFile = mock(RandomAccessFile.class);
        LicensedAudioImpl licensedAudio = new LicensedAudioImpl("EdBangerRecords", 640, "DWT", 640L, 480, new Producer("Test"), new ArrayList<>());
        licensedAudio.setAddress("Address");
        licensedAudio.setUploadDate(new Date());
        try {
            PersistenceManager.saveLicensedAudio(randomAccessFile, seek, licensedAudio);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        try {
            verify(randomAccessFile, atLeastOnce()).seek(seek);
            verify(randomAccessFile, atLeastOnce()).writeUTF("DWT");
            verify(randomAccessFile, atLeastOnce()).writeInt(640);
            verify(randomAccessFile, atLeastOnce()).writeLong(640);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void loadLicensedAudio() {
        RandomAccessFile randomAccessFile = mock(RandomAccessFile.class);
        try {
            LicensedAudioImpl licensedAudio = PersistenceManager.loadLicensedAudio(randomAccessFile);
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

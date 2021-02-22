import persistence.PersistenceManager;
import mediaDB.InteractiveVideo;
import mediaDB.LicensedAudioVideo;
import mediaDB.MediaContent;
import mediaDB.Uploader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JOSTest {

    @Test
    void serializeMedia() {
        ObjectOutput oos = mock(ObjectOutput.class);
        List<MediaContent> items = new ArrayList<>();
        items.add(mock(InteractiveVideo.class));
        items.add(mock(LicensedAudioVideo.class));
        try {
            PersistenceManager.saveJOS(oos, items);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        try {
            verify(oos).writeObject(any());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void serializeUploader() {
        ObjectOutput oos = mock(ObjectOutput.class);
        List<Uploader> items = new ArrayList<>();
        items.add(mock(Uploader.class));
        items.add(mock(Uploader.class));
        try {
            PersistenceManager.saveJOS(oos, items);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        try {
            verify(oos).writeObject(any());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void deserializeMedia() {
        ObjectInput objectInput=mock(ObjectInput.class);
        try {
            List<MediaContent> items = PersistenceManager.loadJOS(objectInput);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            fail();
        }
        try {
            verify(objectInput).readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void deserializeUploader() {
        ObjectInput objectInput=mock(ObjectInput.class);
        try {
            List<Uploader> items = PersistenceManager.loadJOS(objectInput);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            fail();
        }
        try {
            verify(objectInput).readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}

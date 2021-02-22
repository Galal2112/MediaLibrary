import persistence.PersistenceManager;
import mediaDB.InteractiveVideo;
import mediaDB.LicensedAudioVideo;
import mediaDB.MediaContent;
import mediaDB.Uploader;
import org.junit.jupiter.api.Test;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JBPTest {

    @Test
    void saveMediaJBP() {
        XMLEncoder xmlEncoder = mock(XMLEncoder.class);
        List<MediaContent> items = new ArrayList<>();
        items.add(mock(InteractiveVideo.class));
        items.add(mock(LicensedAudioVideo.class));
        try {
            PersistenceManager.saveMediaUsingJBP(xmlEncoder, items);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        verify(xmlEncoder).writeObject(any());
    }

    @Test
    void saveUploaderJBP() {
        XMLEncoder xmlEncoder = mock(XMLEncoder.class);
        List<Uploader> items = new ArrayList<>();
        items.add(mock(Uploader.class));
        items.add(mock(Uploader.class));
        try {
            PersistenceManager.saveUploadersUsingJBP(xmlEncoder, items);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        verify(xmlEncoder).writeObject(any());
    }

    @Test
    void loadMediaJBP() {
        XMLDecoder xmlEncoder = mock(XMLDecoder.class);
        try {
            List<MediaContent> items = PersistenceManager.loadMediaUsingJBP(xmlEncoder);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            fail();
        }
        verify(xmlEncoder).readObject();
    }

    @Test
    void loadUploaderJBP() {
        XMLDecoder xmlEncoder = mock(XMLDecoder.class);
        try {
            List<Uploader> items = PersistenceManager.loadUploaderUsingJBP(xmlEncoder);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            fail();
        }
        verify(xmlEncoder).readObject();
    }
}

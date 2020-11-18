import mediaDB.InteractiveVideo;
import storage.InsufficientStorageException;
import storage.MediaStorage;
import observer.MediaStorageObserver;
import observer.Observer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ObserverTest {

    @Test
    void testObserverPrintStorageWarning() {
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        MediaStorage mediaStorage = Mockito.mock(MediaStorage.class);
        MediaStorageObserver mediaStorageObserver = new MediaStorageObserver(mediaStorage);
        BigDecimal storage = new BigDecimal(100);
        when(mediaStorage.getDiskSize()).thenReturn(storage);
        when(mediaStorage.getAvailableMediaStorageInMB()).thenReturn(new BigDecimal(7));
        mediaStorageObserver.updateObserver();
        assertEquals("⚠ Warning: 93.0% of Storage is Used", outContent.toString().trim());
    }

    @Test
    void testObserverIgnoreLessThan90PercenFilled() {
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        MediaStorage mediaStorage = Mockito.mock(MediaStorage.class);
        MediaStorageObserver mediaStorageObserver = new MediaStorageObserver(mediaStorage);
        BigDecimal storage = new BigDecimal(100);
        when(mediaStorage.getDiskSize()).thenReturn(storage);
        when(mediaStorage.getAvailableMediaStorageInMB()).thenReturn(new BigDecimal(80));
        mediaStorageObserver.updateObserver();
        assertEquals("", outContent.toString().trim());
    }

    @Test
    void testMediaStorageNotifiesObserver() throws InsufficientStorageException {
        Observer observer = Mockito.mock(Observer.class);
        MediaStorage.sharedInstance.register(observer);
        InteractiveVideo interactiveVideo = Mockito.mock(InteractiveVideo.class);
        BigDecimal storage = MediaStorage.sharedInstance.getAvailableMediaStorageInMB();
        when(interactiveVideo.getSize()).thenReturn(storage.divide(new BigDecimal(2)));
        MediaStorage.sharedInstance.addMediaInStorage(interactiveVideo);
        verify(observer).updateObserver();
    }
}

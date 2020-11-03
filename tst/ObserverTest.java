import mediaDB.InteractiveVideo;
import model.MediaStorge;
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
        MediaStorge mediaStorge = Mockito.mock(MediaStorge.class);
        MediaStorageObserver mediaStorageObserver = new MediaStorageObserver(mediaStorge);
        BigDecimal storage = new BigDecimal(100);
        when(mediaStorge.getDiskSize()).thenReturn(storage);
        when(mediaStorge.getAvailableMediaStorageInMB()).thenReturn(new BigDecimal(7));
        mediaStorageObserver.updateObserver();
        assertEquals("⚠ Warning: 93.0% of Storage is Used", outContent.toString().trim());
    }

    @Test
    void testObserverIgnoreLessThan90PercenFilled() {
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        MediaStorge mediaStorge = Mockito.mock(MediaStorge.class);
        MediaStorageObserver mediaStorageObserver = new MediaStorageObserver(mediaStorge);
        BigDecimal storage = new BigDecimal(100);
        when(mediaStorge.getDiskSize()).thenReturn(storage);
        when(mediaStorge.getAvailableMediaStorageInMB()).thenReturn(new BigDecimal(80));
        mediaStorageObserver.updateObserver();
        assertEquals("", outContent.toString().trim());
    }

    @Test
    void testMediaStorageNotifiesObserver() {
        Observer observer = Mockito.mock(Observer.class);
        MediaStorge.sharedInstance.register(observer);
        InteractiveVideo interactiveVideo = Mockito.mock(InteractiveVideo.class);
        BigDecimal storage = MediaStorge.sharedInstance.getAvailableMediaStorageInMB();
        when(interactiveVideo.getSize()).thenReturn(storage.divide(new BigDecimal(2)));
        MediaStorge.sharedInstance.addMediaInStorage(interactiveVideo);
        verify(observer).updateObserver();
    }
}

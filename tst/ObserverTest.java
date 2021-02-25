import businessLogic.MediaAdmin;
import mediaDB.InteractiveVideo;
import mediaDB.Tag;
import observer.MediaStorageObserver;
import observer.Observer;
import observer.TagsObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import storage.InsufficientStorageException;
import storage.MediaStorage;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ObserverTest {

    private MediaStorage mediaStorage;

    @BeforeEach
    void setup() {
        this.mediaStorage = new MediaStorage(10 * 1024);
    }

    @Test
    void testStorageObserverPrintStorageWarning() {
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
    void testStorageObserverIgnoreLessThan90PercentFilled() {
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
        mediaStorage.register(observer);
        InteractiveVideo interactiveVideo = Mockito.mock(InteractiveVideo.class);
        BigDecimal storage = mediaStorage.getAvailableMediaStorageInMB();
        when(interactiveVideo.getSize()).thenReturn(storage.divide(new BigDecimal(2)));
        mediaStorage.addMediaInStorage(interactiveVideo);
        verify(observer).updateObserver();
    }

    @Test
    void testTagsObserverPrintUsedTags() {
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        MediaAdmin mediaAdmin = Mockito.mock(MediaAdmin.class);
        TagsObserver tagsObserver = new TagsObserver(mediaAdmin);
        List<Tag> usedTags = Arrays.asList(Tag.News, Tag.Animal);
        when(mediaAdmin.getUsedTags()).thenReturn(usedTags);
        tagsObserver.updateObserver();
        assertEquals("Used tags updated: " + new HashSet<>(usedTags), outContent.toString().trim());
        verify(mediaAdmin, atLeast(2)).getUsedTags();
    }

    @Test
    void testTagsObserverIgnoreUsedTagsIfNoChange() {
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        MediaAdmin mediaAdmin = Mockito.mock(MediaAdmin.class);
        List<Tag> usedTags = Arrays.asList(Tag.News, Tag.Animal);
        when(mediaAdmin.getUsedTags()).thenReturn(usedTags);
        TagsObserver tagsObserver = new TagsObserver(mediaAdmin);
        tagsObserver.updateObserver();
        assertEquals("", outContent.toString().trim());
        verify(mediaAdmin, atLeast(2)).getUsedTags();
    }
}

import businessLogic.MediaAdmin;
import mediaDB.InteractiveVideo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import storage.InsufficientStorageException;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SimulationTest {

    @Test
    void testSimulation1() throws InsufficientStorageException {
        MediaAdmin mediaAdmin = Mockito.mock(MediaAdmin.class);
        InteractiveVideo interactiveVideo = Mockito.mock(InteractiveVideo.class);
        List<InteractiveVideo> interactiveVideos = Collections.singletonList(interactiveVideo);
        when(mediaAdmin.listMedia(null)).thenReturn((List) interactiveVideos);
        MainSimulation1.startSimulation(mediaAdmin);
        sleep(2);
        verify(mediaAdmin, atLeastOnce()).getUploader(any());
        verify(mediaAdmin, atLeastOnce()).createUploader(any());
        verify(mediaAdmin, atLeastOnce()).upload(any());
        verify(mediaAdmin, atLeastOnce()).listMedia(null);
        verify(mediaAdmin, atLeastOnce()).deleteMedia(interactiveVideo);
        verify(mediaAdmin, never()).deleteUploader(any());
        verify(mediaAdmin, never()).listProducersAndUploadsCount();
        verify(mediaAdmin, never()).retrieveMediaByAddress(any());
    }

    @Test
    void testSimulation2() throws InsufficientStorageException {
        MediaAdmin mediaAdmin = Mockito.mock(MediaAdmin.class);
        InteractiveVideo interactiveVideo = Mockito.mock(InteractiveVideo.class);
        String videoAddress = "InteractiveVideo@Address";
        when(interactiveVideo.getAddress()).thenReturn(videoAddress);
        List<InteractiveVideo> interactiveVideos = Collections.singletonList(interactiveVideo);
        when(mediaAdmin.listMedia(null)).thenReturn((List) interactiveVideos);
        MainSimulation2.startSimulation(mediaAdmin);
        sleep(3);
        verify(mediaAdmin, atLeastOnce()).getUploader(any());
        verify(mediaAdmin, atLeastOnce()).createUploader(any());
        verify(mediaAdmin, atLeastOnce()).upload(any());
        verify(mediaAdmin, atLeastOnce()).listMedia(null);
        verify(mediaAdmin, atLeastOnce()).deleteMedia(interactiveVideo);
        verify(mediaAdmin, atLeastOnce()).retrieveMediaByAddress(videoAddress);
        verify(mediaAdmin, never()).deleteUploader(any());
        verify(mediaAdmin, never()).listProducersAndUploadsCount();
    }

        private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

import businessLogic.MediaAdmin;
import mediaDB.LicensedAudioVideo;
import mvc.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ListenersTest {

    @Test
    void testCreateEvent() throws InterruptedException {
        String createLicensedAudioVideoCommand = "LicensedAudioVideo Produzent1 , 8000 600 DCT 1400 900 MDCT 44100 EdBangerRecords";
        Console console = Mockito.mock(Console.class);
        String readModeTitle = "Mode:";
        when(console.readStringFromStdin(readModeTitle)).thenReturn(":c");
        String readCommandTitle = ">>";
        when(console.readStringFromStdin(readCommandTitle)).thenReturn(createLicensedAudioVideoCommand);

        MediaView mediaView = new CliMediaView(console);

        MediaAdmin mediaAdmin = Mockito.mock(MediaAdmin.class);
        new MediaLibraryController(mediaView, mediaAdmin);

        mediaView.readInput(readModeTitle);
        mediaView.readInput(readCommandTitle);
        verify(mediaAdmin).upload(any(LicensedAudioVideo.class));
    }

    @Test
    void testDeleteEvent()  {
        String deletedProducterName = "Produzent1";
        Console console = Mockito.mock(Console.class);
        String readModeTitle = "Mode:";
        when(console.readStringFromStdin(readModeTitle)).thenReturn(":d");
        String readCommandTitle = ">>";
        when(console.readStringFromStdin(readCommandTitle)).thenReturn(deletedProducterName);

        MediaView mediaView = new CliMediaView(console);

        MediaAdmin mediaAdmin = Mockito.mock(MediaAdmin.class);
        new MediaLibraryController(mediaView, mediaAdmin);

        mediaView.readInput(readModeTitle);
        mediaView.readInput(readCommandTitle);
        verify(mediaAdmin).deleteUploaderByName(deletedProducterName);
    }
}

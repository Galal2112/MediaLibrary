import businessLogic.MediaAdmin;
import events.InputEventHandler;
import mediaDB.LicensedAudioVideo;
import mvc.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ListenersTest {

    @Test
    void testInputEvent() {
        String createLicensedAudioVideoCommand = "LicensedAudioVideo Produzent1 , 8000 600 DCT 1400 900 MDCT 44100 EdBangerRecords";
        Console console = Mockito.mock(Console.class);
        String readModeTitle = "Mode:";
        when(console.readStringFromStdin(readModeTitle)).thenReturn(":c");
        String readCommandTitle = "Command:";
        when(console.readStringFromStdin(readCommandTitle)).thenReturn(createLicensedAudioVideoCommand);

        MediaView mediaView = new CliMediaView(console);

        MediaAdmin mediaAdmin = Mockito.mock(MediaAdmin.class);
        MediaController controller = new MediaLibraryController(mediaView, mediaAdmin);

        InputEventHandler handler = new InputEventHandler();
        handler.add(controller);
        mediaView.setHandler(handler);

        mediaView.readInput(readModeTitle);
        mediaView.readInput(readCommandTitle);
        verify(mediaAdmin).upload(any(LicensedAudioVideo.class));
    }

    /*
    @Test
    void testExitEvent() {
        Console console = Mockito.mock(Console.class);
        MediaView mediaView = new CliMediaView(console);
        when(console.readStringFromStdin("")).thenReturn("exit");
        InputEventHandler handler = new InputEventHandler();
        handler.add(new ExitEventListener());
        mediaView.setHandler(handler);
        System.setSecurityManager(new SecurityManager() {
            @Override
            public void checkExit(int status) {
                super.checkExit(status);
                throw new SecurityException();
            }
        });
        assertThrows(SecurityException.class, () -> mediaView.readInput(""));
    }
     */
}

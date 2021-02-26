import businessLogic.ConsoleLogger;
import mediaDB.MediaContent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ConsoleLoggerTest {

    private final PrintStream systemOut = System.out;

    private ConsoleLogger consoleLogger = new ConsoleLogger();

    @Test
    void testDidCreateUploader() {
        String uploader = "Test uploader";
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        consoleLogger.didCreateUploader(uploader);
        assertEquals("Logger: Did create uploader with name: " + uploader, outContent.toString().trim());
    }

    @Test
    void testUploaderAlreadyRegistered() {
        String uploader = "TestUploader";
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        consoleLogger.uploaderAlreadyRegistered(uploader);
        assertEquals("Logger: Failed to create uploader, \"already registered\"", outContent.toString().trim());
    }
    @Test
    void testDidUpload() {
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        MediaContent media = Mockito.mock(MediaContent.class);
        when(media.getSize()).thenReturn(BigDecimal.valueOf(10));
        System.setOut(new PrintStream(outContent));
        consoleLogger.didUpload(media);
        assertEquals("Logger: Did upload media of size " + media.getSize(), outContent.toString().trim());
    }

    @Test
    void testDidListMedia() {
        int listSize = 10;
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        consoleLogger.didListMedia(listSize);
        assertEquals("Logger: did list media, list size: " + listSize, outContent.toString().trim());
    }
    @Test
    void testDidDeleteUploaderWithName() {
        String UploaderWithName = "Test uploader";
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        consoleLogger.didDeleteUploaderWithName(UploaderWithName);
        assertEquals("Logger: did delete uploader with name: " + UploaderWithName, outContent.toString().trim());
    }

    @AfterEach
    void tearDown() {
        System.setOut(systemOut);
    }
}

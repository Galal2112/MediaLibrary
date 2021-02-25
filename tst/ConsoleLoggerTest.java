import businessLogic.ConsoleLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @AfterEach
    void tearDown() {
        System.setOut(systemOut);
    }
}

import cli.Console;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsoleTest {
    private final InputStream systemIn = System.in;

    @Test
    void testReadStringFromStdin() throws IOException {
        String expectedText = "Test string";
        System.setIn(new ByteArrayInputStream(expectedText.getBytes()));
        Console console = new Console();
        String consoleText = console.readStringFromStdin("");
        assertEquals(consoleText, expectedText);
    }

    @Test
    void testReadLongFromStdin() throws IOException {
        String expectedNumber = "10";
        System.setIn(new ByteArrayInputStream(expectedNumber.getBytes()));
        Console console = new Console();
        long consoleLong = console.readLongFromStdin("");
        assertEquals(consoleLong + "", expectedNumber);
    }

    @AfterEach
    void tearDown() {
        System.setIn(systemIn);
    }
}

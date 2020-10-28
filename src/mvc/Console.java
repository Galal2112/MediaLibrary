package mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console {

    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public String readStringFromStdin(String text) {
        try {
            System.out.print(text);
            return reader.readLine();
        } catch (IOException e) {
            return readStringFromStdin(text);
        }
    }
}

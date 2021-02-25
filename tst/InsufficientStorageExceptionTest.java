import org.junit.jupiter.api.Test;
import storage.InsufficientStorageException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsufficientStorageExceptionTest {

    @Test
    void saveMediaJBP() {
       try {
           throw new InsufficientStorageException();
       } catch (InsufficientStorageException e) {
           assertEquals(e.getMessage(), "Insufficient Storage");
       }
    }
}

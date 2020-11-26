package storage;

public class InsufficientStorageException extends Exception {

    @Override
    public String getMessage() {
        return "Insufficient Storage";
    }
}

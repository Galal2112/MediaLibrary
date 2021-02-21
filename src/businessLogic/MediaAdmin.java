package businessLogic;

import mediaDB.MediaContent;
import mediaDB.Tag;
import mediaDB.Uploadable;
import mediaDB.Uploader;
import observer.Subject;
import storage.InsufficientStorageException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MediaAdmin extends Subject {

    void createUploader(Uploader uploader) throws IllegalArgumentException;
    //can i create interface extends MediaContent & Uploadable and use it instead??
    <T extends MediaContent & Uploadable> void upload(T media) throws IllegalArgumentException, InsufficientStorageException;

    Map<Uploader, Integer> listProducersAndUploadsCount();

    <T extends MediaContent & Uploadable> List<?> listMedia(Class<T> type) throws IllegalArgumentException;

    List<Tag> getAllTags();

    void deleteUploaderByName(String name) throws IllegalArgumentException;

    void deleteUploader(Uploader uploader) throws IllegalArgumentException;

    <T extends MediaContent & Uploadable> void deleteMedia(T media) throws IllegalArgumentException;

    void deleteMediaByAddress(String address) throws IllegalArgumentException;

    Optional<Uploader> getUploader(String name);

   Optional<MediaContent> retrieveMediaByAddress(String address);

    <T extends MediaContent & Uploadable> void update(T media);

    void saveJOS() throws IOException;

    void loadJOS() throws IOException;

    void saveJBP();

    void loadJBP() throws IOException;

    void save(String retrivalAddress) throws IllegalArgumentException;

    MediaContent load(String retrivalAddress) throws IllegalArgumentException, InsufficientStorageException;
}

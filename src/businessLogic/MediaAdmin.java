package businessLogic;

import mediaDB.MediaContent;
import mediaDB.Tag;
import mediaDB.Uploadable;
import mediaDB.Uploader;

import java.util.List;
import java.util.Map;

public interface MediaAdmin {

    void createUploader(Uploader uploader) throws IllegalArgumentException;

    <T extends MediaContent & Uploadable> void upload(T media) throws IllegalArgumentException, InterruptedException;

    Map<Uploader, Integer> listProducersAndUploadsCount();

    <T extends MediaContent & Uploadable> List<?> listMedia(Class<T> type) throws IllegalArgumentException;

    List<Tag> getAllTags();

    void deleteUploaderByName(String name) throws IllegalArgumentException;

    void deleteUploader(Uploader uploader) throws IllegalArgumentException;

    <T extends MediaContent & Uploadable> void deleteMedia(T media) throws IllegalArgumentException;

    void deleteMediaByAddress(String address) throws IllegalArgumentException;
}

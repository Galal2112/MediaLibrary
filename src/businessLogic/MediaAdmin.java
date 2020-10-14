package businessLogic;

import mediaDB.MediaContent;
import mediaDB.Tag;
import mediaDB.Uploadable;
import mediaDB.Uploader;

import java.util.List;
import java.util.Map;

public interface MediaAdmin {
    void createUploader(Uploader uploader);
    <T extends MediaContent & Uploadable> void upload(T media);
    Map<Uploader, Integer> listProducersAndUploadsCount();
    <T extends MediaContent & Uploadable> List<? extends MediaContent> listMedia(Class<T> type);
    List<Tag> getAllTags();
    void deleteUploaderByName(String name);
    void deleteUploader(Uploader uploader);
    <T extends MediaContent & Uploadable> void deleteMedia(T media);
    void deleteMediaByAddress(String address);
}

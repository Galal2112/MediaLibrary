package businessLogic;

import mediaDB.*;

import java.util.List;
import java.util.Map;

public interface MediaAdmin {
    void create(Uploader uploader);
    <T extends MediaContent & Uploadable> void upload(T media);
    Map<Uploader, Integer> listProducersAndUploadsCount();
    <T extends MediaContent & Uploadable> List<?> listMedia(Class<T> type);
    List<Tag> getAllTags();
}

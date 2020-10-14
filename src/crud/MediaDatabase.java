package crud;

import mediaDB.*;

import java.util.Iterator;
import java.util.LinkedList;

class MediaDatabase {
    public static MediaDatabase current = new MediaDatabase();

    private MediaDatabase() {}

    private final LinkedList<InteractiveVideo> interactiveVideoList = new LinkedList<>();
    private final LinkedList<LicensedAudioVideo> licensedAudioVideoList = new LinkedList<>();
    private final LinkedList<Uploader> uploaderList = new LinkedList<>();

    <T> void insert(T entity) {
        if (entity instanceof InteractiveVideo) {
            interactiveVideoList.add((InteractiveVideo) entity);
        } else if (entity instanceof LicensedAudioVideo) {
            licensedAudioVideoList.add((LicensedAudioVideo) entity);
        } else if (entity instanceof Uploader) {
            uploaderList.add((Uploader) entity);
        } else {
            throw new IllegalArgumentException("Table does not exist");
        }
    }

    <T> LinkedList<T> getAll(Class<T> type) {
        if (type == InteractiveVideo.class) {
            return (LinkedList<T>) interactiveVideoList;
        } else if (type == LicensedAudioVideo.class) {
            return (LinkedList<T>) licensedAudioVideoList;
        }  else if (type == Uploader.class) {
            return (LinkedList<T>) uploaderList;
        }
        throw new IllegalArgumentException("Table does not exist");
    }

    <T extends MediaContent & Uploadable> void update(T updatedEntity) {

        LinkedList<T> targetList = (LinkedList<T>) getAll(updatedEntity.getClass());

        Iterator<T> it = targetList.iterator();
        int index = 0;
        while (it.hasNext()) {
            if (it.next().getAddress().equals(updatedEntity.getAddress())) {
                targetList.set(index, updatedEntity);
                break;
            }
            index ++;
        }
    }
}

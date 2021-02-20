package businessLogic;

import crud.CRUD;
import mediaDB.MediaContent;
import mediaDB.Tag;
import mediaDB.Uploadable;
import mediaDB.Uploader;
import storage.InsufficientStorageException;
import storage.MediaStorage;

import java.util.*;
import java.util.stream.Collectors;

public class MediaLibraryAdmin implements MediaAdmin {

    private final CRUD<Uploader> uploaderCRUD;
    private final CRUD<MediaContent> mediaContentCRUD;
    private volatile Logger logger;
    private MediaStorage mediaStorage;

    public MediaLibraryAdmin(MediaStorage mediaStorage, CRUD<Uploader> uploaderCRUD, CRUD<MediaContent> mediaContentCRUD) {
        this.mediaStorage = mediaStorage;
        this.uploaderCRUD = uploaderCRUD;
        this.mediaContentCRUD = mediaContentCRUD;
    }

    public synchronized void setLogger(Logger logger) {
        this.logger = logger;
    }

    public synchronized void stopLogger() {
        this.logger = null;
    }

    @Override
    public synchronized void createUploader(Uploader uploader) throws IllegalArgumentException {
        if (!uploaderCRUD.get(uploader.getName()).isPresent()) {
            uploaderCRUD.create(uploader);
            if (logger != null) logger.didCreateUploader(uploader.getName());
        } else {
            if (logger != null) logger.uploaderAlreadyRegistered(uploader.getName());
            throw new IllegalArgumentException("Username is taken");
        }
    }

    @Override
    public synchronized <T extends MediaContent & Uploadable>  void upload(T media) throws IllegalArgumentException, InsufficientStorageException {

        // check producer exists
        Optional<Uploader> optionalUploader = uploaderCRUD.get(media.getUploader().getName());
        if (!optionalUploader.isPresent()) {
            throw new IllegalArgumentException("Producer does not exist");
        }

        // Set address
        media.setAddress(getAddress(media));

        // set date
        media.setUploadDate(new Date());

        // save media content
        mediaContentCRUD.create(media);

        mediaStorage.addMediaInStorage(media);

        if (logger != null) logger.didUpload(media);
    }

    @Override
    public synchronized Map<Uploader, Integer> listProducersAndUploadsCount() {
        List<Uploader> producers = uploaderCRUD.getAll();
        HashMap<Uploader, Integer> producerUploadCount = new HashMap<>();
        producers.forEach(producer -> producerUploadCount.put(producer, 0));
        List<Uploadable> uploadsList = mediaContentCRUD.getAll().stream().map(media -> (Uploadable) media)
                .collect(Collectors.toList());
        uploadsList.forEach(media -> {
            int count = producerUploadCount.get(media.getUploader());
            producerUploadCount.put(media.getUploader(), count + 1);
        });
        if (logger != null) logger.didListProducersAndUploadsCount();
        return producerUploadCount;
    }

    @Override
    public synchronized <T extends MediaContent & Uploadable> List<?> listMedia(Class<T> type) throws IllegalArgumentException {
        List<MediaContent> result = new LinkedList<>();
        if (type == null) {
            result.addAll(mediaContentCRUD.getAll());
        } else {
            // Get media data based on type using isInstance
            result.addAll(mediaContentCRUD.getAll().stream().filter(m -> m.getClass().equals(type)).collect(Collectors.toList()));
        }

        // update access count
        for (MediaContent content : result) {
            content.setAccessCount(content.getAccessCount() + 1);
            //update acessCount in the DB
            mediaContentCRUD.update(content);
        }
        if (logger != null) logger.didListMedia(result.size());
        return result;
    }

    @Override
    public synchronized List<Tag> getAllTags() {
        if (logger != null) logger.didListTags();
        return Arrays.asList(Tag.values());
    }

    @Override
    public synchronized void deleteUploaderByName(String name) throws IllegalArgumentException {
        if (uploaderCRUD.get(name).isPresent()) {
            uploaderCRUD.deleteById(name);
            List<MediaContent> uploaderMediaList = mediaContentCRUD.getAll().stream().filter(m -> ((Uploadable) m).getUploader().getName().equals(name)).collect(Collectors.toList());
            for (MediaContent mediaContent : uploaderMediaList) {
                mediaContentCRUD.deleteById(mediaContent.getAddress());
            }
            if (logger != null) logger.didDeleteUploaderWithName(name);
        } else {
            throw new IllegalArgumentException("Uploader name doesn't exist");
        }

    }

    @Override
    public synchronized void deleteUploader(Uploader uploader) throws IllegalArgumentException {
        deleteUploaderByName(uploader.getName());
    }

    @Override
    public synchronized <T extends MediaContent & Uploadable> void deleteMedia(T media) throws IllegalArgumentException {
        deleteMediaByAddress(media.getAddress());
    }

    @Override
    public synchronized void deleteMediaByAddress(String address) throws IllegalArgumentException {
            Optional<MediaContent> mediaContentOptional =  mediaContentCRUD.get(address);
        if (mediaContentOptional.isPresent()) {
            mediaStorage.deletedMediaFromStorage(mediaContentOptional.get());
            mediaContentCRUD.deleteById(address);
            if (logger != null) logger.didDeleteMediaAtAddress(address);
        } else {
            throw new IllegalArgumentException("Media doesn't exist");
        }
    }

    @Override
    public Optional<Uploader> getUploader(String name) {
        Optional<Uploader> uploaderOptional = uploaderCRUD.get(name);
        if (logger != null) {
            if (uploaderOptional.isPresent()) {
                logger.didRetrieveUploader(name);
            } else {
                logger.requestedUploaderNotFount(name);
            }
        }
        return uploaderOptional;
    }

    @Override
    public Optional<MediaContent> retrieveMediaByAddress(String address) {
        Optional<MediaContent> media = mediaContentCRUD.get(address);
        media.ifPresent(mediaContent -> mediaContent.setAccessCount(mediaContent.getAccessCount() + 1));
        if (logger != null) {
            if (media.isPresent()) {
                logger.didRetrieveMediaAtAddress(address);
            } else {
                logger.mediaNotFoundAtAddress(address);
            }
        }
        return media;
    }

    @Override
    public <T extends MediaContent & Uploadable> void update(T media) {
        mediaContentCRUD.update(media);
    }

    private static int address = 0;
    private String getAddress(Object o) {
        return address++ + "@" + o.getClass().getSimpleName();
    }


}

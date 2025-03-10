package businessLogic;

import crud.CRUD;
import mediaDB.MediaContent;
import mediaDB.Tag;
import mediaDB.Uploadable;
import mediaDB.Uploader;
import observer.Observer;
import persistence.PersistenceHelper;
import storage.InsufficientStorageException;
import storage.MediaStorage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class MediaLibraryAdmin implements MediaAdmin {

    private static final String serMediaFileName = "media.ser";
    private static final String xmlMediaFileName = "media.xml";
    private static final String serUploadersFileName = "uploaders.ser";
    private static final String xmlUploadersFileName = "uploaders.xml";

    private static final String mediaFileName = "media.data";
    private static final String mediaIndexFileName = "media.idx";

    private final CRUD<Uploader> uploaderCRUD;
    private final CRUD<MediaContent> mediaContentCRUD;
    private volatile Logger logger;
    private MediaStorage mediaStorage;
    private PersistenceHelper persistenceHelper;
    private final ConcurrentLinkedQueue<Observer> observerList = new ConcurrentLinkedQueue<>();

    public MediaLibraryAdmin(MediaStorage mediaStorage, CRUD<Uploader> uploaderCRUD, CRUD<MediaContent> mediaContentCRUD) {
        this(new PersistenceHelper(), mediaStorage, uploaderCRUD, mediaContentCRUD);
    }

    public MediaLibraryAdmin(PersistenceHelper persistenceHelper, MediaStorage mediaStorage, CRUD<Uploader> uploaderCRUD, CRUD<MediaContent> mediaContentCRUD) {
        this.persistenceHelper = persistenceHelper;
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
            notifyObserver();
            if (logger != null) logger.didCreateUploader(uploader.getName());
        } else {
            if (logger != null) logger.uploaderAlreadyRegistered(uploader.getName());
            throw new IllegalArgumentException("Username is taken");
        }
    }

    @Override
    public synchronized <T extends MediaContent & Uploadable> void upload(T media) throws IllegalArgumentException, InsufficientStorageException {

        // check producer exists
        Optional<Uploader> optionalUploader = uploaderCRUD.get(media.getUploader().getName());
        if (!optionalUploader.isPresent()) {
            throw new IllegalArgumentException("Producer does not exist");
        }

        // Set address
        media.setAddress(getAddress());

        // set date
        media.setUploadDate(new Date());

        mediaStorage.addMediaInStorage(media);

        // save media content
        mediaContentCRUD.create(media);

        notifyObserver();
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

        if (logger != null) logger.didListMedia(result.size());
        return result;
    }

    @Override
    public synchronized List<Tag> getAllTags() {
        if (logger != null) logger.didListTags();
        return Arrays.asList(Tag.values());
    }

    @Override
    public List<Tag> getUsedTags() {
        Set<Tag> tagsSet = new LinkedHashSet<>();
        List<MediaContent> mediaList = mediaContentCRUD.getAll();
        for (MediaContent media : mediaList) {
            tagsSet.addAll(media.getTags());
        }
        return new ArrayList<>(tagsSet);
    }

    @Override
    public List<Tag> getUnusedTags() {
        List<Tag> usedTags = getUsedTags();
        return Arrays.stream(Tag.values()).filter(tag -> !usedTags.contains(tag)).collect(Collectors.toList());
    }

    @Override
    public synchronized void deleteUploaderByName(String name) throws IllegalArgumentException {
        if (uploaderCRUD.get(name).isPresent()) {
            uploaderCRUD.deleteById(name);
            List<MediaContent> uploaderMediaList = mediaContentCRUD.getAll().stream().filter(m -> ((Uploadable) m).getUploader().getName().equals(name)).collect(Collectors.toList());
            for (MediaContent mediaContent : uploaderMediaList) {
                mediaContentCRUD.deleteById(mediaContent.getAddress());
            }
            notifyObserver();
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
        Optional<MediaContent> mediaContentOptional = mediaContentCRUD.get(address);
        if (mediaContentOptional.isPresent()) {
            mediaStorage.deletedMediaFromStorage(mediaContentOptional.get());
            mediaContentCRUD.deleteById(address);
            notifyObserver();
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
        media.ifPresent(mediaContent -> {
            mediaContent.setAccessCount(mediaContent.getAccessCount() + 1);
            mediaContentCRUD.update(mediaContent);
            notifyObserver();
        });
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
        notifyObserver();
    }

    @Override
    public void saveJOS() throws IOException {
        try {
            persistenceHelper.saveJOS(mediaContentCRUD.getAll(), serMediaFileName);
            persistenceHelper.saveJOS(uploaderCRUD.getAll(), serUploadersFileName);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void loadJOS() throws IOException, InsufficientStorageException {
        try {
            List<MediaContent> mediaList = persistenceHelper.loadJOS(serMediaFileName);
            List<Uploader> uploaders = persistenceHelper.loadJOS(serUploadersFileName);
            save(uploaders, mediaList);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveJBP() {
        try {
            persistenceHelper.saveMediaUsingJBP(xmlMediaFileName, mediaContentCRUD.getAll());
            persistenceHelper.saveUploadersUsingJBP(xmlUploadersFileName, uploaderCRUD.getAll());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void loadJBP() throws IOException, InsufficientStorageException {

        try {
            List<MediaContent> mediaList = persistenceHelper.loadMediaUsingJBP(xmlMediaFileName);
            List<Uploader> uploaders = persistenceHelper.loadUploaderUsingJBP(xmlUploadersFileName);
            save(uploaders, mediaList);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(String retrivalAddress) throws IllegalArgumentException {
        Optional<MediaContent> media = mediaContentCRUD.get(retrivalAddress);
        if (media.isEmpty()) {
            throw new IllegalArgumentException("Media not found");
        }
        try {
            persistenceHelper.saveRandom(media.get(), mediaIndexFileName, mediaFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MediaContent load(String retrivalAddress) throws IllegalArgumentException, InsufficientStorageException {
        try {

            MediaContent mediaContent = persistenceHelper.loadRandom(retrivalAddress, mediaIndexFileName, mediaFileName);

            if (mediaContent != null) {
                if (mediaContentCRUD.get(retrivalAddress).isPresent()) {
                    mediaContentCRUD.update(mediaContent);
                } else {
                    mediaStorage.addMediaInStorage(mediaContent);
                    Uploadable uploadable = (Uploadable) mediaContent;
                    uploaderCRUD.create(uploadable.getUploader());
                    mediaContentCRUD.create(mediaContent);
                }
            }

            return mediaContent;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void register(Observer observer) {
        if (observer == null) return;
        observerList.add(observer);
    }

    @Override
    public void unregister(Observer observer) {
        if (observer == null) return;
        observerList.remove(observer);
    }

    @Override
    public void notifyObserver() {
        for (Observer observer : observerList) {
            observer.updateObserver();
        }
    }

    private void save(List<Uploader> uploaders, List<MediaContent> mediaList) throws InsufficientStorageException {
        BigDecimal requiredSize = new BigDecimal(0);
        for (MediaContent m : mediaList) {
            requiredSize = requiredSize.add(m.getSize());
        }
        if (requiredSize.compareTo(mediaStorage.getDiskSize()) <= 0) {
            mediaStorage.clear();
            uploaderCRUD.drop();
            mediaContentCRUD.drop();
            // insert uploaders
            uploaders.forEach(uploader -> {
                if (getUploader(uploader.getName()).isEmpty()) {
                    createUploader(uploader);
                }
            });
            // save media content
            mediaList.forEach(media -> {
                try {
                    mediaStorage.addMediaInStorage(media);
                    mediaContentCRUD.create(media);
                } catch (InsufficientStorageException e) {
                    e.printStackTrace();
                }
            });
        } else {
            throw new InsufficientStorageException();
        }
    }

    private String getAddress() {
        return new Date().getTime() + "@" + UUID.randomUUID().toString();
    }

}

package businessLogic;

import crud.CRUD;
import mediaDB.*;
import model.MediaStorage;

import java.util.*;
import java.util.stream.Collectors;

public class MediaLibraryAdmin implements MediaAdmin {

    private final CRUD<Uploader> uploaderCRUD;
    private final CRUD<MediaContent> mediaContentCRUD;

    public MediaLibraryAdmin(CRUD<Uploader> uploaderCRUD, CRUD<MediaContent> mediaContentCRUD) {
        this.uploaderCRUD = uploaderCRUD;
        this.mediaContentCRUD = mediaContentCRUD;
    }

    @Override
    public void createUploader(Uploader uploader) throws IllegalArgumentException {
        if (!uploaderCRUD.get(uploader.getName()).isPresent()) {
            uploaderCRUD.create(uploader);
        } else {
            throw new IllegalArgumentException("Username is taken");
        }
    }

    @Override
    public <T extends MediaContent & Uploadable> void upload(T media) throws IllegalArgumentException, InterruptedException {

        if (!(media instanceof InteractiveVideo) && !(media instanceof LicensedAudioVideo)) {
            throw new IllegalArgumentException("Unsupported media type");
        }

        // check producer exists
        Optional<Uploader> optionalUploader = uploaderCRUD.get(media.getUploader().getName());
        if (!optionalUploader.isPresent()) {
            throw new IllegalArgumentException("Producer does not exist");
        }

        // Set address
        media.setAddress(getAddress(media));

        MediaStorage.sharedInstance.addMediaInStorage(media);

        // set date
        media.setUploadDate(new Date());

        // save media content
        mediaContentCRUD.create(media);
    }

    @Override
    public Map<Uploader, Integer> listProducersAndUploadsCount() {
        List<Uploader> producers = uploaderCRUD.getAll();
        HashMap<Uploader, Integer> producerUploadCount = new HashMap<>();
        producers.forEach(producer -> producerUploadCount.put(producer, 0));
        List<Uploadable> uploadsList = mediaContentCRUD.getAll().stream().map(media -> (Uploadable) media)
                .collect(Collectors.toList());
        uploadsList.forEach(media -> {
            int count = producerUploadCount.get(media.getUploader());
            producerUploadCount.put(media.getUploader(), count + 1);
        });
        return producerUploadCount;
    }

    @Override
    public <T extends MediaContent & Uploadable> List<?> listMedia(Class<T> type) throws IllegalArgumentException {
        List<MediaContent> result = new LinkedList<>();
        if (type == null) {
            result.addAll(mediaContentCRUD.getAll());
        } else {
            // Get media data based on type using isInstance
            result.addAll(mediaContentCRUD.getAll().stream().filter(type :: isInstance).collect(Collectors.toList()));
        }

        // update access count
        for (MediaContent content : result) {
            content.setAccessCount(content.getAccessCount() + 1);
            //update acessCount in the DB
            mediaContentCRUD.update(content);
        }
        return result;
    }

    @Override
    public List<Tag> getAllTags() {
        return Arrays.asList(Tag.values());
    }

    @Override
    public void deleteUploaderByName(String name) throws IllegalArgumentException {
        if (uploaderCRUD.get(name).isPresent()) {
            uploaderCRUD.deleteById(name);
        } else {
            throw new IllegalArgumentException("Uploader name doesn't exist");
        }

    }

    @Override
    public void deleteUploader(Uploader uploader) throws IllegalArgumentException {
        if (uploaderCRUD.get(uploader.getName()).isPresent()) {
            uploaderCRUD.delete(uploader);
        } else {
            throw new IllegalArgumentException("Uploader name doesn't exist");

        }
    }

    @Override
    public <T extends MediaContent & Uploadable> void deleteMedia(T media) throws IllegalArgumentException {
        if (!(media instanceof InteractiveVideo) && !(media instanceof LicensedAudioVideo)) {
            throw new IllegalArgumentException("Unsupported media type");
        }
        if (mediaContentCRUD.get(media.getAddress()).isPresent()) {
            MediaStorage.sharedInstance.deletedMediaFromStorage(media);
            mediaContentCRUD.delete(media);
        }
    }

    @Override
    public void deleteMediaByAddress(String address) throws IllegalArgumentException {

        if (mediaContentCRUD.get(address).isPresent()) {
            MediaStorage.sharedInstance.deleteMediaByAddress(address);
            mediaContentCRUD.deleteById(address);
        } else {
            throw new IllegalArgumentException("Invalid Address");
        }
    }

    private String getAddress(Object o) {
        return o.getClass().getSimpleName() + '@' + Integer.toHexString(o.hashCode());
    }

}

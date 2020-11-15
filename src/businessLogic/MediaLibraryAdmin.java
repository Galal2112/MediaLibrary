package businessLogic;

import crud.CRUD;
import mediaDB.*;
import model.MediaStorge;

import java.util.*;

public class MediaLibraryAdmin implements MediaAdmin {

    private final CRUD<Uploader> uploaderCRUD;
    private final CRUD<InteractiveVideo> interactiveVideoCRUD;
    private final CRUD<LicensedAudioVideo> licensedAudioVideoCRUD;

    public MediaLibraryAdmin(CRUD<Uploader> uploaderCRUD, CRUD<InteractiveVideo> interactiveVideoCRUD,
                             CRUD<LicensedAudioVideo> licensedAudioVideoCRUD) {
        this.uploaderCRUD = uploaderCRUD;
        this.interactiveVideoCRUD = interactiveVideoCRUD;
        this.licensedAudioVideoCRUD = licensedAudioVideoCRUD;
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
    public <T extends MediaContent & Uploadable> void upload(T media) throws IllegalArgumentException {

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

        MediaStorge.sharedInstance.addMediaInStorage(media);

        // set date
        media.setUploadDate(new Date());

        // save media content
        if (media instanceof InteractiveVideo) {
            interactiveVideoCRUD.create((InteractiveVideo) media);
        } else {
            licensedAudioVideoCRUD.create((LicensedAudioVideo) media);
        }
    }

    @Override
    public Map<Uploader, Integer> listProducersAndUploadsCount() {
        List<Uploader> producers = uploaderCRUD.getAll();
        HashMap<Uploader, Integer> producerUploadCount = new HashMap<>();
        producers.forEach(producer -> producerUploadCount.put(producer, 0));
        List<Uploadable> uploadsList = new LinkedList<>();
        uploadsList.addAll(interactiveVideoCRUD.getAll());
        uploadsList.addAll(licensedAudioVideoCRUD.getAll());
        uploadsList.forEach(media -> {
            int count = producerUploadCount.get(media.getUploader());
            producerUploadCount.put(media.getUploader(), count + 1);
        });
        return producerUploadCount;
    }

    @Override
    // return interactiveVideo or lincensedAudioVideo
    public <T extends MediaContent & Uploadable> List<?> listMedia(Class<T> type) throws IllegalArgumentException {
        List<MediaContent> result = new LinkedList<>();
        if (type == null) {
            result.addAll(interactiveVideoCRUD.getAll());
            result.addAll(licensedAudioVideoCRUD.getAll());
        } else if (type == InteractiveVideo.class) {
            result.addAll(interactiveVideoCRUD.getAll());
        } else if (type == LicensedAudioVideo.class) {
            result.addAll(licensedAudioVideoCRUD.getAll());
        } else {
            throw new IllegalArgumentException("Unsupported media type");
        }

        // update access count
        for (MediaContent content : result) {
            content.setAccessCount(content.getAccessCount() + 1);
            //update acessCount in the DB
            if (content instanceof InteractiveVideo) {
                interactiveVideoCRUD.update((InteractiveVideo) content);
            } else {
                licensedAudioVideoCRUD.update((LicensedAudioVideo) content);
            }
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
        if (interactiveVideoCRUD.get(media.getAddress()).isPresent() || licensedAudioVideoCRUD.get(media.getAddress()).isPresent()) {
            MediaStorge.sharedInstance.deletedMediaFromStorage(media);
            if (media instanceof InteractiveVideo) {
                interactiveVideoCRUD.delete((InteractiveVideo) media);
            } else {
                licensedAudioVideoCRUD.delete((LicensedAudioVideo) media);
            }
        }
    }

    @Override
    public void deleteMediaByAddress(String address) throws IllegalArgumentException {

        if (interactiveVideoCRUD.get(address).isPresent() || licensedAudioVideoCRUD.get(address).isPresent()) {
            MediaStorge.sharedInstance.deleteMediaByAddress(address);
            // if address belongs to InteractiveVideo ... delete
            interactiveVideoCRUD.deleteById(address);

            // if address belongs to InteractiveVideo ... delete
            licensedAudioVideoCRUD.deleteById(address);
        } else {
            throw new IllegalArgumentException("Invalid Address");
        }
    }

    private String getAddress(Object o) {
        return o.getClass().getSimpleName() + '@' + Integer.toHexString(o.hashCode());
    }

}

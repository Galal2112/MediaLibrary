package businessLogic;

import crud.CRUD;
import mediaDB.*;

import java.math.BigDecimal;
import java.util.*;

public class MediaLibraryAdmin implements MediaAdmin {

    // 1 Gigabyte storage
    public static final BigDecimal availableStorage = new BigDecimal(1024 * 1024 * 1024);

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
    public void createUploader(Uploader uploader) {
        if (!uploaderCRUD.get(uploader.getName()).isPresent()) {
            uploaderCRUD.create(uploader);
        } else {
            throw new IllegalArgumentException("Username is taken");
        }
    }

    @Override
    public <T extends MediaContent & Uploadable> void upload(T media) {

        if (!(media instanceof InteractiveVideo) && !(media instanceof LicensedAudioVideo)) {
            throw new IllegalArgumentException("Unsupported media type");
        }

        // check producer exists
        Optional<Uploader> optionalUploader = uploaderCRUD.get(media.getUploader().getName());
        if (!optionalUploader.isPresent()) {
            throw new IllegalArgumentException("Producer does not exist");
        }

        // check size
        if (availableStorage.compareTo(media.getSize()) < 0) {
            throw new IllegalArgumentException("Insufficient storage");
        }

        // Set address
        media.setAddress(getAddress(media));

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
    public <T extends MediaContent & Uploadable> List<?> listMedia(Class<T> type) {
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
            //update acessCount in the DBx
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
    public void deleteUploaderByName(String name) {
        uploaderCRUD.deleteById(name);
    }

    @Override
    public void deleteUploader(Uploader uploader) {
        uploaderCRUD.delete(uploader);
    }

    @Override
    public <T extends MediaContent & Uploadable> void deleteMedia(T media) {
        if (!(media instanceof InteractiveVideo) && !(media instanceof LicensedAudioVideo)) {
            throw new IllegalArgumentException("Unsupported media type");
        }

        if (media instanceof InteractiveVideo) {
            interactiveVideoCRUD.delete((InteractiveVideo) media);
        } else {
            licensedAudioVideoCRUD.delete((LicensedAudioVideo) media);
        }
    }

    @Override
    public void deleteMediaByAddress(String address) {
        // if address belongs to InteractiveVideo ... delete
        interactiveVideoCRUD.deleteById(address);

        // if address belongs to InteractiveVideo ... delete
        licensedAudioVideoCRUD.deleteById(address);
    }

    private String getAddress(Object o) {
        return o.getClass().getName() + '@' + Integer.toHexString(o.hashCode());
    }

}

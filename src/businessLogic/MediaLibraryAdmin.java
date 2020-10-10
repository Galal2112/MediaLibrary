package businessLogic;

import crud.InteractiveVideoCRUD;
import crud.LicensedAudioVideoCRUD;
import crud.UploaderCRUD;
import mediaDB.*;

import java.math.BigDecimal;
import java.util.*;

public class MediaLibraryAdmin implements MediaAdmin {

    // 1 Gigabyte storage
    private static final BigDecimal availableStorage = new BigDecimal(1024 * 1024 * 1024);

    private final UploaderCRUD uploaderCRUD = new UploaderCRUD();
    private final InteractiveVideoCRUD interactiveVideoCRUD = new InteractiveVideoCRUD();
    private final LicensedAudioVideoCRUD licensedAudioVideoCRUD = new LicensedAudioVideoCRUD();

    @Override
    public void create(Uploader uploader) {
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
        UploaderCRUD producerCRUD = new UploaderCRUD();

        Optional<Uploader> optionalUploader = producerCRUD.get(media.getUploader().getName());
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
        List<Uploadable> uploadsList = new ArrayList<>();
        uploadsList.addAll(interactiveVideoCRUD.getAll());
        uploadsList.addAll(licensedAudioVideoCRUD.getAll());
        uploadsList.forEach(media -> {
            int count = producerUploadCount.get(media.getUploader());
            producerUploadCount.put(media.getUploader(), count + 1);
        });
        return producerUploadCount;
    }

    @Override
    public <T extends MediaContent & Uploadable> List<?> listMedia(Class<T> type) {
        if (type == null) {
            List<Uploadable> uploadsList = new ArrayList<>();
            uploadsList.addAll(interactiveVideoCRUD.getAll());
            uploadsList.addAll(licensedAudioVideoCRUD.getAll());
            return uploadsList;
        }
        if (type == InteractiveVideo.class) {
            return interactiveVideoCRUD.getAll();
        }

        if (type == LicensedAudioVideo.class) {
            return licensedAudioVideoCRUD.getAll();
        }
        throw new IllegalArgumentException("Unsupported media type");
    }

    @Override
    public List<Tag> getAllTags() {
        return Arrays.asList(Tag.values());
    }

    private String getAddress(Object o) {
        return o.getClass().getName() + '@' + Integer.toHexString(o.hashCode());
    }

}

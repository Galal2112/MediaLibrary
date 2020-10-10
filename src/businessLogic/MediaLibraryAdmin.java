package businessLogic;

import dao.InteractiveVideoDao;
import dao.LicensedAudioVideoDao;
import dao.UploaderDao;
import mediaDB.*;

import java.math.BigDecimal;
import java.util.*;

public class MediaLibraryAdmin implements MediaAdmin {

    // 1 Gigabyte storage
    private static final BigDecimal availableStorage = new BigDecimal(1024 * 1024 * 1024);

    private final UploaderDao uploaderDao = new UploaderDao();
    private final InteractiveVideoDao interactiveVideoDao = new InteractiveVideoDao();
    private final LicensedAudioVideoDao licensedAudioVideoDao = new LicensedAudioVideoDao();

    @Override
    public void create(Uploader uploader) {
        if (!uploaderDao.get(uploader.getName()).isPresent()) {
            uploaderDao.create(uploader);
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
        UploaderDao producerDao = new UploaderDao();

        Optional<Uploader> optionalUploader = producerDao.get(media.getUploader().getName());
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
            interactiveVideoDao.create((InteractiveVideo) media);
        } else {
            licensedAudioVideoDao.create((LicensedAudioVideo) media);
        }
    }

    @Override
    public Map<Uploader, Integer> listProducersAndUploadsCount() {
        List<Uploader> producers = uploaderDao.getAll();
        HashMap<Uploader, Integer> producerUploadCount = new HashMap<>();
        producers.forEach(producer -> producerUploadCount.put(producer, 0));
        List<Uploadable> uploadsList = new ArrayList<>();
        uploadsList.addAll(interactiveVideoDao.getAll());
        uploadsList.addAll(licensedAudioVideoDao.getAll());
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
            uploadsList.addAll(interactiveVideoDao.getAll());
            uploadsList.addAll(licensedAudioVideoDao.getAll());
            return uploadsList;
        }
        if (type == InteractiveVideo.class) {
            return interactiveVideoDao.getAll();
        }

        if (type == LicensedAudioVideo.class) {
            return licensedAudioVideoDao.getAll();
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

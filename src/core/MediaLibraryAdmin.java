package core;

import dao.MediaDao;
import dao.ProducerDao;
import mediaDB.MediaContent;
import mediaDB.Uploadable;
import model.Producer;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

public class MediaLibraryAdmin implements MediaAdmin {
    private ProducerDao producerDao = new ProducerDao();
    private MediaDao mediaDao = new MediaDao();

    // 1 Gigabyte storage
    private static BigDecimal availableStorage = new BigDecimal(1024 * 1024 * 1024);

    @Override
    public <T extends MediaContent & Uploadable> void upload(T media) {

        // check producer exists
        ProducerDao producerDao = new ProducerDao();

        Optional<Producer> optionalProducer = producerDao.get(media.getUploader().getName());
        if (!optionalProducer.isPresent()) {
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

        Producer producer = optionalProducer.get();
        int uploadsCount = producer.getUploadsCount();
        producer.setUploadsCount(uploadsCount + 1);
        // save media content


    }

    private String getAddress(Object o) {
        return o.getClass().getName() + '@' + Integer.toHexString(o.hashCode());
    }

}

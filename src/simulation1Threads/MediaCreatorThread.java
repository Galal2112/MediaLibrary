package simulation1Threads;

import businessLogic.MediaAdmin;
import mediaDB.MediaContent;
import mediaDB.Uploadable;
import mediaDB.Uploader;
import mediaDB.Video;
import observer.Observer;
import storage.InsufficientStorageException;
import storage.MediaStorage;
import util.RandomGenerator;

import java.math.BigDecimal;

public class MediaCreatorThread extends Thread implements Observer {

    private final MediaAdmin mediaAdmin;
    private final MediaStorage mediaStorage;

    public MediaCreatorThread(MediaAdmin mediaAdmin, MediaStorage mediaStorage) {
        this.mediaAdmin = mediaAdmin;
        this.mediaStorage = mediaStorage;
        mediaStorage.register(this);
    }

    @Override
    public void run() {
        while (true) {

                try {
                    Video randomVideo = RandomGenerator.getRandomMedia();
                    try {
                        createUploaderIfNotExist(randomVideo.getUploader());
                        mediaAdmin.upload(randomVideo);
                        printMedia(randomVideo);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (InsufficientStorageException e) {
                        // No space available
                        System.out.println("No space available");
                        synchronized (this) {
                            wait();
                        }
                    }
                } catch (InterruptedException ie) {
                    mediaStorage.unregister(this);
                }
            }
    }

    private void createUploaderIfNotExist(Uploader uploader) {
        if (!mediaAdmin.getUploader(uploader.getName()).isPresent()) {
            mediaAdmin.createUploader(uploader);
        }
    }

    @Override
    public void updateObserver() {
        if (getState() == State.WAITING) {
            BigDecimal hardTotalSize = mediaStorage.getDiskSize();
            BigDecimal freeSize = mediaStorage.getAvailableMediaStorageInMB();
            float freeSizePercent = freeSize.divide(hardTotalSize).floatValue() * 100;
            if (freeSizePercent >= 20) {
                System.out.println("New space available " + freeSize.doubleValue());
                synchronized (this) {
                    notify();
                }
            }
        }
    }

    private <T extends Uploadable & MediaContent> void printMedia(T media) {
        System.out.println("Bitrate: " + media.getBitrate() + " Length: " + media.getLength().toMinutes() + " Size: " + media.getSize().doubleValue());
    }
}

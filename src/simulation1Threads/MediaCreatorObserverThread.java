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

public class MediaCreatorObserverThread extends Thread implements Observer {

    private final MediaAdmin mediaAdmin;
    private final MediaStorage mediaStorage;

    public MediaCreatorObserverThread(MediaAdmin mediaAdmin, MediaStorage mediaStorage) {
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
                        printUploadedMedia(randomVideo);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (InsufficientStorageException e) {
                        // No space available
                        System.out.println(getName() + " did receive insufficient storage message");
                        synchronized (this) {
                            wait();
                            System.out.println(getName() + " moved to waiting state \"Insufficient Storage\"");

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
            System.out.println(getName() + " created new uploader with name: " + uploader.getName());
        }
    }

    @Override
    public void updateObserver() {
        if (getState() == State.WAITING) {
            BigDecimal hardTotalSize = mediaStorage.getDiskSize();
            BigDecimal freeSize = mediaStorage.getAvailableMediaStorageInMB();
            float freeSizePercent = freeSize.divide(hardTotalSize).floatValue() * 100;
            if (freeSizePercent >= 20) {
                System.out.println(getName() + " was notified by storage, space available " + freeSize.doubleValue());
                synchronized (this) {
                    notify();
                    System.out.println(getName() + " moved to running state");

                }
            }
        }
    }

    private <T extends Uploadable & MediaContent> void printUploadedMedia(T media) {
        System.out.println(getName() + " did upload video of size: " + media.getSize().doubleValue());
    }
}

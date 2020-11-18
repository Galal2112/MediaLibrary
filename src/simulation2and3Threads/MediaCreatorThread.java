package simulation2and3Threads;

import businessLogic.MediaAdmin;
import mediaDB.MediaContent;
import mediaDB.Uploadable;
import mediaDB.Uploader;
import mediaDB.Video;
import storage.InsufficientStorageException;
import storage.MediaStorage;
import util.RandomGenerator;

public class MediaCreatorThread extends Thread {

    private final MediaAdmin mediaAdmin;
    private final MediaStorage mediaStorage;

    public MediaCreatorThread(MediaAdmin mediaAdmin, MediaStorage mediaStorage) {
        this.mediaAdmin = mediaAdmin;
        this.mediaStorage = mediaStorage;
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
                        System.out.println(Thread.currentThread().getName() + ": No space available");
                        synchronized (mediaStorage) {
                            mediaStorage.wait();
                        }
                    }
                } catch (InterruptedException ie) {
                    System.out.println(Thread.currentThread().getName() + ": Thread interrupted");
                }
            }
    }

    private void createUploaderIfNotExist(Uploader uploader) {
        if (!mediaAdmin.getUploader(uploader.getName()).isPresent()) {
            mediaAdmin.createUploader(uploader);
        }
    }

    private <T extends Uploadable & MediaContent> void printMedia(T media) {
        System.out.println(Thread.currentThread().getName() + ": Bitrate: " + media.getBitrate() + " Length: " + media.getLength().toMinutes() + " Size: " + media.getSize().doubleValue());
    }
}

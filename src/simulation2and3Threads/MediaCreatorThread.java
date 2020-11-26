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
            synchronized (mediaStorage) {
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
                        System.out.println(getName() + " did receive insufficient storage message");

                        mediaStorage.wait();
                        System.out.println(getName() + " moved to waiting state \"Insufficient Storage\"");
                    }
                } catch (InterruptedException ie) {
                    System.out.println(Thread.currentThread().getName() + " was interrupted");
                }
            }
        }
    }

    private void createUploaderIfNotExist(Uploader uploader) {
        if (!mediaAdmin.getUploader(uploader.getName()).isPresent()) {
            mediaAdmin.createUploader(uploader);
            System.out.println(getName() + " created new uploader with name: " + uploader.getName());
        }
    }

    private <T extends Uploadable & MediaContent> void printMedia(T media) {
        System.out.println(currentThread().getName() + " did upload video of size: " + media.getSize().doubleValue());
    }
}

package simulation2and3Threads;

import businessLogic.MediaAdmin;
import mediaDB.Audio;
import mediaDB.MediaContent;
import mediaDB.Video;
import storage.MediaStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Simultation3DeletionThread extends Thread {

    private MediaAdmin mediaAdmin;
    private MediaStorage mediaStorage;
    private final Random rand = new Random(); //instance of random class

    public Simultation3DeletionThread(MediaAdmin mediaAdmin, MediaStorage mediaStorage) {
        this.mediaAdmin = mediaAdmin;
        this.mediaStorage = mediaStorage;
    }

    @Override
    public void run() {
        while (true) {
            List<?> mediaContentList = mediaAdmin.listMedia(null);
            if (mediaContentList.size() == 0) {
                continue;
            }
            int deletionCount = rand.nextInt(mediaContentList.size() + 1);
            if (deletionCount == 0) {
                continue;
            }
            System.out.println(Thread.currentThread().getName() + " will delete " + deletionCount + " media files");
            // sort descending based on access count
            Collections.sort(mediaContentList, Comparator.comparingLong(video -> ((MediaContent) video).getAccessCount()));
            for (int i = 0; i < deletionCount; i++) {
                MediaContent media = (MediaContent) mediaContentList.get(i);
                if (media instanceof Video) {
                    mediaAdmin.deleteMedia((Video) media);
                } else if (media instanceof Audio) {
                    mediaAdmin.deleteMedia((Audio) media);
                }

                System.out.println(Thread.currentThread().getName() + " deleted media of size: " + media.getSize());
            }
            synchronized (mediaStorage) {
                mediaStorage.notifyAll();
                System.out.println(currentThread().getName() + " notify all threads after the deletion of media");
            }
        }
    }
}
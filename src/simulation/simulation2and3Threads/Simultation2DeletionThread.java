package simulation.simulation2and3Threads;

import businessLogic.MediaAdmin;
import mediaDB.Audio;
import mediaDB.MediaContent;
import mediaDB.Video;
import storage.MediaStorage;

import java.util.List;

public class Simultation2DeletionThread extends Thread {

    private final MediaAdmin mediaAdmin;
    private final MediaStorage mediaStorage;

    public Simultation2DeletionThread(MediaAdmin mediaAdmin, MediaStorage mediaStorage) {
        this.mediaAdmin = mediaAdmin;
        this.mediaStorage = mediaStorage;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (mediaStorage) {
                List<?> mediaContentList = mediaAdmin.listMedia(null);
                System.out.println(getName() + " retrieved media list of size: " + mediaContentList.size());
                if (mediaContentList.size() == 0) {
                    continue;
                }
                long lowestAccessCount = Long.MAX_VALUE;
                MediaContent mediaWithFewestRequests = null;
                for (Object media : mediaContentList) {
                    MediaContent mediaContent = (MediaContent) media;
                    if (mediaContent.getAccessCount() < lowestAccessCount) {
                        mediaWithFewestRequests = mediaContent;
                        lowestAccessCount = mediaContent.getAccessCount();
                    }
                }
                if (mediaWithFewestRequests != null) {
                    if (mediaWithFewestRequests instanceof Video) {
                        mediaAdmin.deleteMedia((Video) mediaWithFewestRequests);
                    } else if (mediaWithFewestRequests instanceof Audio) {
                        mediaAdmin.deleteMedia((Audio) mediaWithFewestRequests);
                    }
                    System.out.println(getName() + " delete the media with lowest access count, deleted size: " + mediaWithFewestRequests.getSize());
                    mediaStorage.notifyAll();
                    System.out.println(currentThread().getName() + " notify all threads after the deletion of media");
                }
            }
        }
    }
}

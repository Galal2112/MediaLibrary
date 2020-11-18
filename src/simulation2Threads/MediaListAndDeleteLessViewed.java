package simulation2Threads;

import businessLogic.MediaAdmin;
import mediaDB.Audio;
import mediaDB.MediaContent;
import mediaDB.Video;
import storage.MediaStorage;

import java.util.List;

public class MediaListAndDeleteLessViewed extends Thread {

    private MediaAdmin mediaAdmin;
    private MediaStorage mediaStorage;

    public MediaListAndDeleteLessViewed(MediaAdmin mediaAdmin, MediaStorage mediaStorage) {
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
                System.out.println("Delete media with size: " + mediaWithFewestRequests.getSize());
                synchronized (mediaStorage) {
                    mediaStorage.notifyAll();
                }
            }
        }
    }
}

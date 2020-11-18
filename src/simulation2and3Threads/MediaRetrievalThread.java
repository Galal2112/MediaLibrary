package simulation2and3Threads;

import businessLogic.MediaAdmin;
import mediaDB.Content;
import util.RandomGenerator;

import java.util.List;

public class MediaRetrievalThread extends Thread {

    private final MediaAdmin mediaAdmin;

    public MediaRetrievalThread(MediaAdmin mediaAdmin) {
        this.mediaAdmin = mediaAdmin;
    }

    @Override
    public void run() {
        while (true) {
            List<?> mediaContentList = mediaAdmin.listMedia(null);
            if (mediaContentList.size() == 0) {
                continue;
            }
            int randomIndex = RandomGenerator.getBoundedRandomNumber(mediaContentList.size()) - 1;
            Content media = (Content) mediaContentList.get(randomIndex);
            // This method increases the access count
            mediaAdmin.retrieveMediaByAddress(media.getAddress());
            System.out.println("Update access count");
        }
    }
}

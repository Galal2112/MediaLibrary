package simulation1Threads;

import businessLogic.MediaAdmin;
import mediaDB.Audio;
import mediaDB.Video;
import util.RandomGenerator;

import java.util.List;

public class MediaListAndRandomDeleteThread extends Thread {

    private MediaAdmin mediaAdmin;

    public MediaListAndRandomDeleteThread(MediaAdmin mediaAdmin) {
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
            Object media = mediaContentList.get(randomIndex);
            if (media instanceof Video) {
                mediaAdmin.deleteMedia((Video) mediaContentList.get(randomIndex));
            } else if (media instanceof Audio) {
                mediaAdmin.deleteMedia((Audio) mediaContentList.get(randomIndex));
            }
        }
    }
}

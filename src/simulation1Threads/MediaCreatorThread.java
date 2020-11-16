package simulation1Threads;

import businessLogic.MediaAdmin;
import mediaDB.InteractiveVideo;
import mediaDB.LicensedAudioVideo;
import mediaDB.Uploader;
import util.RandomGenerator;

public class MediaCreatorThread extends Thread {

    private MediaAdmin mediaAdmin;

    public MediaCreatorThread(MediaAdmin mediaAdmin) {
        this.mediaAdmin = mediaAdmin;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int random = RandomGenerator.getBoundedRandomNumber(2);
                if (random == 1) {
                    InteractiveVideo interactiveVideo = RandomGenerator.getRandomMedia(InteractiveVideo.class);
                    createUploaderIfNotExist(interactiveVideo.getUploader());
                    mediaAdmin.upload(interactiveVideo);
                } else {
                    LicensedAudioVideo licensedAudioVideo = RandomGenerator.getRandomMedia(LicensedAudioVideo.class);
                    createUploaderIfNotExist(licensedAudioVideo.getUploader());
                    mediaAdmin.upload(licensedAudioVideo);
                }
            } catch (InterruptedException | IllegalArgumentException e) {
                e.printStackTrace();
            }

        }
    }

    private void createUploaderIfNotExist(Uploader uploader) {
        if (!mediaAdmin.getUploader(uploader.getName()).isPresent()) {
            mediaAdmin.createUploader(uploader);
        }
    }
}

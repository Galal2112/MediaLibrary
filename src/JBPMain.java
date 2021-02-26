import businessLogic.MediaAdmin;
import mediaDB.Uploader;
import mediaDB.Video;
import storage.InsufficientStorageException;
import storage.MediaStorage;
import util.MediaAdminFactory;
import util.RandomGenerator;

import java.io.IOException;

public class JBPMain {

    public static void main(String[] args) {
        MediaStorage storage = new MediaStorage(10 * 1000);
        MediaAdmin mediaAdmin = MediaAdminFactory.getMediaAdminInstance(storage);
        // Create filled warehouse
        for (int i = 0; i < 5; i++) {
            Video randomVideo = RandomGenerator.getRandomMedia();
            createUploaderIfNotExist(mediaAdmin, randomVideo.getUploader());
            try {
                Thread.sleep(1);
                mediaAdmin.upload(randomVideo);
            } catch (InsufficientStorageException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Media data");
        mediaAdmin.listMedia(null).forEach(v -> System.out.println(v.toString()));
        System.out.println("----------------------------------------");
        System.out.println("Save JBP");
        mediaAdmin.saveJBP();
        System.out.println("----------------------------------------");
        try {
            System.out.println("Load JBP");
            mediaAdmin.loadJBP();
            mediaAdmin.listMedia(null).forEach(v -> System.out.println(v.toString()));
        } catch (IOException | InsufficientStorageException e) {
            e.printStackTrace();
        }
    }

    private static void createUploaderIfNotExist(MediaAdmin mediaAdmin, Uploader uploader) {
        if (mediaAdmin.getUploader(uploader.getName()).isEmpty()) {
            mediaAdmin.createUploader(uploader);
        }
    }
}

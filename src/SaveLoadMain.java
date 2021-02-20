import businessLogic.MediaAdmin;
import mediaDB.MediaContent;
import mediaDB.Uploader;
import mediaDB.Video;
import storage.InsufficientStorageException;
import storage.MediaStorage;
import util.MediaAdminFactory;
import util.RandomGenerator;

import java.util.List;

public class SaveLoadMain {

    public static void main(String[] args) {
        MediaStorage storage = new MediaStorage(10 * 1000);
        MediaAdmin mediaAdmin = MediaAdminFactory.getMediaAdminInstance(storage);
        for (int i = 0; i < 2; i++) {
            Video randomVideo = RandomGenerator.getRandomMedia();
            createUploaderIfNotExist(mediaAdmin, randomVideo.getUploader());
            try {
                mediaAdmin.upload(randomVideo);
            } catch (InsufficientStorageException e) {
                e.printStackTrace();
            }
        }

        List<?> allMedia = mediaAdmin.listMedia(null);

        System.out.println("Create media and call save");
        for (Object media : allMedia) {
            MediaContent mediaContent = (MediaContent) media;
            mediaAdmin.save(mediaContent.getAddress());
            System.out.println(mediaContent.toString());
        }

        System.out.println("----------------------------------------");

        System.out.println("Loaded Media:");
        // Load
        loadMedia(allMedia, mediaAdmin);

        System.out.println("----------------------------------------");
        System.out.println("Update media access count and save again:");
        for (Object o : allMedia) {
            MediaContent m = (MediaContent) o;
            mediaAdmin.retrieveMediaByAddress(m.getAddress());
            mediaAdmin.save(m.getAddress());
        }
        System.out.println("----------------------------------------");
        System.out.println("Update loaded after update:");
        loadMedia(allMedia, mediaAdmin);
    }

    private static void loadMedia(List<?> allMedia, MediaAdmin mediaAdmin) {
        for (Object media : allMedia) {
            MediaContent mediaContent = null;
            try {
                mediaContent = mediaAdmin.load(((MediaContent) media).getAddress());
            } catch (InsufficientStorageException e) {
                e.printStackTrace();
            }

            if (mediaContent != null) {
                System.out.println(mediaContent.toString());
            } else {
                System.out.println("Not found");
            }
        }
    }

    private static void createUploaderIfNotExist(MediaAdmin mediaAdmin, Uploader uploader) {
        if (mediaAdmin.getUploader(uploader.getName()).isEmpty()) {
            mediaAdmin.createUploader(uploader);
        }
    }
}

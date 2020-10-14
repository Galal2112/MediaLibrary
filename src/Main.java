import businessLogic.MediaAdmin;
import businessLogic.MediaLibraryAdmin;
import crud.InteractiveVideoCRUD;
import crud.LicensedAudioVideoCRUD;
import crud.UploaderCRUD;
import mediaDB.InteractiveVideo;
import mediaDB.Uploader;
import model.InteractiveVideoImpl;
import model.Producer;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        InteractiveVideoCRUD interactiveVideoCRUD = new InteractiveVideoCRUD();
        LicensedAudioVideoCRUD licensedAudioVideoCRUD = new LicensedAudioVideoCRUD();
        UploaderCRUD uploaderCRUD = new UploaderCRUD();
        MediaAdmin mediaAdmin = new MediaLibraryAdmin(uploaderCRUD, interactiveVideoCRUD, licensedAudioVideoCRUD);
        // create producer
        Producer producer = new Producer("Producer 1");
        mediaAdmin.createUploader(producer);

        // upload video
        InteractiveVideo interactiveVideo = new InteractiveVideoImpl("", 1920, 1080, "mp4",
                30, Duration.ofMinutes(5), new BigDecimal(25 * 1024 * 1024), producer);
        mediaAdmin.upload(interactiveVideo);

        System.out.println("Upload date: " + interactiveVideo.getUploadDate());
        System.out.println("Video address: " + interactiveVideo.getAddress());

        Map<Uploader, Integer> videosCountMap = mediaAdmin.listProducersAndUploadsCount();
        System.out.println("Uploder - videos count: ");
        for (Uploader uploader : videosCountMap.keySet()) {
            System.out.println(uploader.getName() + " - " + videosCountMap.get(uploader));
        }
    }
}

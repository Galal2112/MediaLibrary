import businessLogic.MediaAdmin;
import simulation1Threads.MediaCreatorThread;
import simulation1Threads.MediaListAndRandomDeleteThread;
import storage.MediaStorage;
import util.MediaAdminFactory;

public class MainSimulation1 {

    public static void main(String[] args) {

        MediaAdmin mediaAdmin = MediaAdminFactory.getMediaAdminInstance();
        new MediaCreatorThread(mediaAdmin, MediaStorage.sharedInstance).start();
        new MediaListAndRandomDeleteThread(mediaAdmin).start();
    }
}

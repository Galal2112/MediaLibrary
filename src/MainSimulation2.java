import businessLogic.MediaAdmin;
import simulation2and3Threads.MediaCreatorThread;
import simulation2and3Threads.MediaRetrievalThread;
import simulation2and3Threads.Simultation2DeletionThread;
import storage.MediaStorage;
import util.MediaAdminFactory;

public class MainSimulation2 {

    public static void main(String[] args) {

        MediaAdmin mediaAdmin = MediaAdminFactory.getMediaAdminInstance();

        new MediaCreatorThread(mediaAdmin, MediaStorage.sharedInstance).start();
        new MediaRetrievalThread(mediaAdmin).start();
        new Simultation2DeletionThread(mediaAdmin, MediaStorage.sharedInstance).start();
    }
}

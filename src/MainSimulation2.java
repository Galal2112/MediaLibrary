import businessLogic.MediaAdmin;
import simulation2and3Threads.MediaCreatorThread;
import simulation2and3Threads.MediaRetrievalThread;
import simulation2and3Threads.Simultation2DeletionThread;
import storage.MediaStorage;
import util.MediaAdminFactory;

public final class MainSimulation2 {

    private MainSimulation2() {
    }

    public static void startSimulation(MediaAdmin mediaAdmin) {
        new MediaCreatorThread(mediaAdmin, MediaStorage.sharedInstance).start();
        new MediaRetrievalThread(mediaAdmin).start();
        new Simultation2DeletionThread(mediaAdmin, MediaStorage.sharedInstance).start();
    }

    public static void main(String[] args) {

        MediaAdmin mediaAdmin = MediaAdminFactory.getMediaAdminInstance();
        startSimulation(mediaAdmin);
    }
}

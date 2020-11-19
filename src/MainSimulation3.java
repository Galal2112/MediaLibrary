import businessLogic.MediaAdmin;
import simulation2and3Threads.MediaCreatorThread;
import simulation2and3Threads.MediaRetrievalThread;
import simulation2and3Threads.Simultation3DeletionThread;
import storage.MediaStorage;
import util.MediaAdminFactory;

public final class MainSimulation3 {

    private MainSimulation3() {
    }

    public static void startSimulation(MediaAdmin mediaAdmin) {
        new MediaCreatorThread(mediaAdmin, MediaStorage.sharedInstance).start();
        new MediaCreatorThread(mediaAdmin, MediaStorage.sharedInstance).start();
        new MediaRetrievalThread(mediaAdmin).start();
        new Simultation3DeletionThread(mediaAdmin, MediaStorage.sharedInstance).start();
        new Simultation3DeletionThread(mediaAdmin, MediaStorage.sharedInstance).start();
    }

    public static void main(String[] args) {
        MediaAdmin mediaAdmin = MediaAdminFactory.getMediaAdminInstance();
        startSimulation(mediaAdmin);
    }
}

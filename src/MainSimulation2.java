import businessLogic.MediaAdmin;
import simulation2and3Threads.MediaCreatorThread;
import simulation2and3Threads.MediaRetrievalThread;
import simulation2and3Threads.Simultation2DeletionThread;
import storage.MediaStorage;
import util.MediaAdminFactory;

public final class MainSimulation2 {

    private MainSimulation2() {
    }

    public static void startSimulation(MediaAdmin mediaAdmin, MediaStorage mediaStorage) {
        new MediaCreatorThread(mediaAdmin, mediaStorage).start();
        new MediaRetrievalThread(mediaAdmin).start();
        new Simultation2DeletionThread(mediaAdmin, mediaStorage).start();
    }

    public static void main(String[] args) {
        MediaStorage mediaStorage = new MediaStorage(10 * 1024);
        MediaAdmin mediaAdmin = MediaAdminFactory.getMediaAdminInstance(mediaStorage);
        startSimulation(mediaAdmin, mediaStorage);
    }
}

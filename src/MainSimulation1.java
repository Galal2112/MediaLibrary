import businessLogic.MediaAdmin;
import simulation1Threads.MediaCreatorObserverThread;
import simulation1Threads.MediaListAndRandomDeleteThread;
import storage.MediaStorage;
import util.MediaAdminFactory;

public final class MainSimulation1 {

    private MainSimulation1() {
    }

    public static void startSimulation(MediaAdmin mediaAdmin, MediaStorage mediaStorage) {
        new MediaCreatorObserverThread(mediaAdmin, mediaStorage).start();
        new MediaListAndRandomDeleteThread(mediaAdmin).start();
    }

    public static void main(String[] args) {
        MediaStorage mediaStorage = new MediaStorage(10 * 1024);
        MediaAdmin mediaAdmin = MediaAdminFactory.getMediaAdminInstance(mediaStorage);
        startSimulation(mediaAdmin, mediaStorage);
    }
}

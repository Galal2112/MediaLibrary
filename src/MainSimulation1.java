import businessLogic.MediaAdmin;
import simulation1Threads.MediaCreatorObserverThread;
import simulation1Threads.MediaListAndRandomDeleteThread;
import storage.MediaStorage;
import util.MediaAdminFactory;

public final class MainSimulation1 {

    private MainSimulation1() {
    }

    public static void startSimulation(MediaAdmin mediaAdmin) {
        new MediaCreatorObserverThread(mediaAdmin, MediaStorage.sharedInstance).start();
        new MediaListAndRandomDeleteThread(mediaAdmin).start();
    }

    public static void main(String[] args) {
        MediaAdmin mediaAdmin = MediaAdminFactory.getMediaAdminInstance();
        startSimulation(mediaAdmin);
    }
}

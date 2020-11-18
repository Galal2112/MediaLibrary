import businessLogic.MediaAdmin;
import businessLogic.MediaLibraryAdmin;
import crud.CRUD;
import crud.MediaCRUD;
import crud.UploaderCRUD;
import mediaDB.MediaContent;
import simulation2and3Threads.MediaCreatorThread;
import simulation2and3Threads.Simultation2DeletionThread;
import simulation2and3Threads.MediaRetrievalThread;
import storage.MediaStorage;

public class MainSimulation2 {
    public static void main(String[] args) {

        // create media admin
        CRUD<MediaContent> mediaCRUD = new MediaCRUD();
        UploaderCRUD uploaderCRUD = new UploaderCRUD();
        MediaAdmin mediaAdmin = new MediaLibraryAdmin(uploaderCRUD, mediaCRUD);

        new MediaCreatorThread(mediaAdmin, MediaStorage.sharedInstance).start();
        new MediaRetrievalThread(mediaAdmin).start();
        new Simultation2DeletionThread(mediaAdmin, MediaStorage.sharedInstance).start();
    }
}

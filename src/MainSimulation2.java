import businessLogic.MediaAdmin;
import businessLogic.MediaLibraryAdmin;
import crud.CRUD;
import crud.MediaCRUD;
import crud.UploaderCRUD;
import mediaDB.MediaContent;
import simulation2Threads.MediaCreatorThread;
import simulation2Threads.MediaListAndDeleteLessViewed;
import simulation2Threads.MediaRetrievalThread;
import storage.MediaStorage;

public class MainSimulation2 {
    public static void main(String[] args) {

        // create media admin
        CRUD<MediaContent> mediaCRUD = new MediaCRUD();
        UploaderCRUD uploaderCRUD = new UploaderCRUD();
        MediaAdmin mediaAdmin = new MediaLibraryAdmin(uploaderCRUD, mediaCRUD);

        new MediaCreatorThread(mediaAdmin,MediaStorage.sharedInstance).start();
        new MediaRetrievalThread(mediaAdmin).start();
        new MediaListAndDeleteLessViewed(mediaAdmin,MediaStorage.sharedInstance).start();
    }
}

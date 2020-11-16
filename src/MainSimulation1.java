import businessLogic.MediaAdmin;
import businessLogic.MediaLibraryAdmin;
import crud.CRUD;
import crud.MediaCRUD;
import crud.UploaderCRUD;
import mediaDB.MediaContent;
import simulation1Threads.MediaCreatorThread;
import simulation1Threads.MediaListAndRandomDeleteThread;

public class MainSimulation1 {

    public static void main(String[] args) {

        // create media admin
        CRUD<MediaContent> mediaCRUD = new MediaCRUD();
        UploaderCRUD uploaderCRUD = new UploaderCRUD();
        MediaAdmin mediaAdmin = new MediaLibraryAdmin(uploaderCRUD, mediaCRUD);

        new MediaCreatorThread(mediaAdmin).start();
        new MediaListAndRandomDeleteThread(mediaAdmin).start();
    }
}

import businessLogic.MediaAdmin;
import businessLogic.MediaLibraryAdmin;
import crud.CRUD;
import crud.MediaCRUD;
import crud.UploaderCRUD;
import mediaDB.MediaContent;
import model.MediaStorage;
import mvc.*;
import observer.MediaStorageObserver;

public class Main {
    public static void main(String[] args) {
        //Observer
        MediaStorageObserver observer = new MediaStorageObserver(MediaStorage.sharedInstance);

        // create media admin
        CRUD<MediaContent> mediaCRUD = new MediaCRUD();
        UploaderCRUD uploaderCRUD = new UploaderCRUD();
        MediaAdmin mediaAdmin = new MediaLibraryAdmin(uploaderCRUD, mediaCRUD);

        // create view
        Console console = new Console();
        MediaView view = new CliMediaView(console);

        // create controller
        MediaController controller = new MediaLibraryController(view, mediaAdmin);
        controller.start();

    }
}

import businessLogic.MediaAdmin;
import businessLogic.MediaLibraryAdmin;
import crud.CRUD;
import crud.MediaCRUD;
import crud.UploaderCRUD;
import mediaDB.MediaContent;
import storage.MediaStorage;
import mvc.*;
import observer.MediaStorageObserver;

public class Main {
    public static void main(String[] args) {

        Console console = new Console();
        long diskSizeGB = console.readLongFromStdin("Enter Disk size in gigabyte:");
        MediaStorage mediaStorage = new MediaStorage(diskSizeGB * 1000);
        //Observer
        MediaStorageObserver observer = new MediaStorageObserver(mediaStorage);

        // create media admin
        CRUD<MediaContent> mediaCRUD = new MediaCRUD();
        UploaderCRUD uploaderCRUD = new UploaderCRUD();
        MediaAdmin mediaAdmin = new MediaLibraryAdmin(mediaStorage, uploaderCRUD, mediaCRUD);

        // create view
        MediaView view = new CliMediaView(console);

        // create controller
        MediaController controller = new MediaLibraryController(view, mediaAdmin);
        controller.start();

    }
}

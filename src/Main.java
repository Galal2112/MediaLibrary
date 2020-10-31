import businessLogic.MediaAdmin;
import businessLogic.MediaLibraryAdmin;
import crud.InteractiveVideoCRUD;
import crud.LicensedAudioVideoCRUD;
import crud.UploaderCRUD;
import events.InputEventHandler;
import model.MediaStorge;
import mvc.*;
import observer.MediaStorageObserver;

public class Main {
    public static void main(String[] args) {
        //Observer
        MediaStorageObserver observer = new MediaStorageObserver(MediaStorge.sharedInstance);

        // create media admin
        InteractiveVideoCRUD interactiveVideoCRUD = new InteractiveVideoCRUD();
        LicensedAudioVideoCRUD licensedAudioVideoCRUD = new LicensedAudioVideoCRUD();
        UploaderCRUD uploaderCRUD = new UploaderCRUD();
        MediaAdmin mediaAdmin = new MediaLibraryAdmin(uploaderCRUD, interactiveVideoCRUD, licensedAudioVideoCRUD);

        // create view
        Console console = new Console();
        MediaView view = new CliMediaView(console);

        // create controller
        MediaController controller = new MediaLibraryController(view, mediaAdmin);

        // set handler and listener
        InputEventHandler handler = new InputEventHandler();
        handler.add(controller);
        view.setHandler(handler);

        controller.start();

    }
}

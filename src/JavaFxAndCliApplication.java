import businessLogic.MediaAdmin;
import businessLogic.MediaLibraryAdmin;
import crud.CRUD;
import crud.MediaCRUD;
import crud.UploaderCRUD;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import mediaDB.MediaContent;
import cli.CliMediaView;
import cli.Console;
import cli.MediaLibraryCliController;
import cli.MediaView;
import gui.MainController;
import storage.MediaStorage;

import java.util.Optional;

public class JavaFxAndCliApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        TextInputDialog dialog = new TextInputDialog("10");
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        TextField inputField = dialog.getEditor();
        BooleanBinding isInvalid = Bindings.createBooleanBinding(() -> isInvalidSize(inputField.getText()), inputField.textProperty());
        okButton.disableProperty().bind(isInvalid);
        dialog.setTitle("Disk Size");
        dialog.setHeaderText("Please Enter disk size Gigabyte:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String input = result.get();
            MediaStorage mediaStorage = new MediaStorage(Long.parseLong(input) * 1000);
            // create media admin
            CRUD<MediaContent> mediaCRUD = new MediaCRUD();
            UploaderCRUD uploaderCRUD = new UploaderCRUD();
            MediaAdmin mediaAdmin = new MediaLibraryAdmin(mediaStorage, uploaderCRUD, mediaCRUD);

            MainController controller = new MainController(mediaAdmin, mediaStorage);
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("main.fxml"));
            loader.setControllerFactory(type -> {
                if (type == MainController.class) {
                    return controller ;
                } else {
                    throw new IllegalArgumentException("Unexpected controller type: " + type);
                }
            });
            Parent root = loader.load();
            primaryStage.setTitle("Media Library");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            MediaLibraryCliController cliController = cliController(mediaAdmin, mediaStorage);
            Thread cliThread = new Thread(cliController::start);
            cliThread.setDaemon(true);
            cliThread.start();
        }
    }

    private MediaLibraryCliController cliController(MediaAdmin mediaAdmin, MediaStorage storage) {
        Console console = new Console();
        // create view
        MediaView view = new CliMediaView(console);

        // create controller
        return new MediaLibraryCliController(view, mediaAdmin, storage);
    }

    private boolean isInvalidSize(String input) {
        try {
            Long.parseLong(input);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

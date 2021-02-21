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
import mvc.CliMediaView;
import mvc.Console;
import mvc.MediaLibraryCliController;
import mvc.MediaView;
import mvvm.MainController;
import storage.MediaStorage;

import java.util.Optional;

public class CommonMain extends Application {

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

            MainController controller = new MainController(mediaAdmin);
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

            MediaLibraryCliController cliController = cliController(mediaAdmin);
            Thread cliThread = new Thread(cliController::start);
            cliThread.setDaemon(true);
            cliThread.start();
        }
    }

    private MediaLibraryCliController cliController(MediaAdmin mediaAdmin) {
        Console console = new Console();
        // create view
        MediaView view = new CliMediaView(console);

        // create controller
        return new MediaLibraryCliController(view, mediaAdmin);
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

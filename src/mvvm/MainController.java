package mvvm;

import businessLogic.MediaAdmin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import mediaDB.InteractiveVideo;
import mediaDB.LicensedAudioVideo;
import mediaDB.Tag;
import model.InteractiveVideoImpl;
import model.LicensedAudioVideoImpl;
import model.Producer;
import storage.InsufficientStorageException;
import util.MediaAdminFactory;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    @FXML private TableView<MediaItemWithProperties> tableview;
    @FXML private TableColumn<MediaItemWithProperties, String> producerColumn;
    @FXML private TableColumn<MediaItemWithProperties, String> addressColumn;
    @FXML private TableColumn<MediaItemWithProperties, String> dateColumn;
    @FXML private TableColumn<MediaItemWithProperties, Long> accessCountColumn;
    @FXML private TextField createMediaTextField;

    private ObservableList<MediaItemWithProperties> mediaObservableList;
    private final MediaAdmin mediaAdmin;

    public MainController()  {
        mediaAdmin = MediaAdminFactory.getMediaAdminInstance();

        mediaObservableList = FXCollections.observableArrayList();
        mediaObservableList.addAll(mediaAdmin.listMedia(null).stream().map(MediaItemWithProperties::new).collect(Collectors.toList()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.producerColumn.setCellValueFactory(cellData -> cellData.getValue().producerProperty());
        this.addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        this.dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        this.accessCountColumn.setCellValueFactory(cellData -> cellData.getValue().accessCountProperty().asObject());
        this.tableview.setItems(mediaObservableList);
        this.tableview.setRowFactory(tableview -> new MediaListCell());
    }

    public synchronized void uploadMedia(ActionEvent actionEvent) {
        String creationCommand = createMediaTextField.getText();
        handleCreateEvent(creationCommand);
    }

    private void handleCreateEvent(String inputText) {
        String[] parsedString = inputText.split(" ");

        if (parsedString.length == 1) {
            createProducer(inputText);
            return;
        }

        try {
            String mediaType = parsedString[0];
            Producer producer = new Producer(parsedString[1]);
            String[] inputTags = parsedString[2].split(",");
            List<Tag> tags = new ArrayList<>();
            for (String inputTag : inputTags) {
                try {
                    tags.add(Tag.valueOf(inputTag));
                } catch (IllegalArgumentException e) {
                    // Non existing tags
                }
            }

            long bitrate = Long.parseLong(parsedString[3]);
            long durationInSeconds = Long.parseLong(parsedString[4]);
            Duration duration = Duration.ofSeconds(durationInSeconds);

            String videoEncoding = parsedString[5];
            int height = Integer.parseInt(parsedString[6]);
            int width = Integer.parseInt(parsedString[7]);

            if (isInteractiveVideo(mediaType)) {
                InteractiveVideo interactiveVideo = new InteractiveVideoImpl(mediaType, width, height,
                        videoEncoding, bitrate, duration, producer);
                interactiveVideo.setTags(tags);
                mediaAdmin.upload(interactiveVideo);
                mediaObservableList.add(new MediaItemWithProperties(interactiveVideo));
            } else if (isLicensedAudioVideo(mediaType)) {
                String audioEncoding = parsedString[8];
                int samplingRate = Integer.parseInt(parsedString[9]);
                String holder = parsedString[10];
                LicensedAudioVideo licensedAudioVideo = new LicensedAudioVideoImpl(samplingRate, width, height,
                        audioEncoding, holder, bitrate, duration, producer);
                licensedAudioVideo.setTags(tags);
                mediaAdmin.upload(licensedAudioVideo);
                mediaObservableList.add(new MediaItemWithProperties(licensedAudioVideo));
            } else {
                displayError("Unsupported Media type");
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            displayError("Invalid insert command");
        } catch (IllegalArgumentException | InsufficientStorageException e) {
            displayError(e.getMessage());
        }
    }

    private boolean isInteractiveVideo(String mediaType) {
        return mediaType.equals("InteractiveVideo");
    }

    private boolean isLicensedAudioVideo(String mediaType) {
        return mediaType.equals("LicensedAudioVideo");
    }

    private void createProducer(String text) {
        Producer producer = new Producer(text);
        try {
            mediaAdmin.createUploader(producer);
        } catch (IllegalArgumentException e) {
            displayError(e.getMessage());
        }
    }

    private void displayError(String error) {

    }

}

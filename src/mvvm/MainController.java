package mvvm;

import businessLogic.MediaAdmin;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import mediaDB.*;
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

    @FXML private TableView<ProducerAndUploadsCount> producerTableView;
    @FXML private TableView<MediaItemWithProperties> mediaTableView;
    @FXML private TableColumn<MediaItemWithProperties, String> producerColumn;
    @FXML private TableColumn<MediaItemWithProperties, String> addressColumn;
    @FXML private TableColumn<MediaItemWithProperties, String> dateColumn;
    @FXML private TableColumn<MediaItemWithProperties, Long> accessCountColumn;
    @FXML private TextField createMediaTextField;
    @FXML private TextField deleteMediaTextField;
    @FXML private TableColumn<ProducerAndUploadsCount, String> allProducersColumn;
    @FXML private TableColumn<ProducerAndUploadsCount, String> uploadsCountColumn;
    @FXML private ComboBox<String> typebox;
    @FXML private Button deleteMediaButton;
    @FXML private Button deleteUploaderButton;

    private ObservableList<MediaItemWithProperties> mediaObservableList;
    private ObservableList<ProducerAndUploadsCount> producersObservableList;
    private final MediaAdmin mediaAdmin;
    private TableColumn<ProducerAndUploadsCount, String> producersSortColumn = null;
    private TableColumn.SortType producerSortType = null;
    private TableColumn<MediaItemWithProperties, String> mediaSortColumn = null;
    private TableColumn.SortType mediaSortType = null;
    private String selectedType;
    private SimpleBooleanProperty isDeleteMediaVisible = new SimpleBooleanProperty();
    private SimpleBooleanProperty isDeleteUploaderVisible = new SimpleBooleanProperty();

    public MainController()  {
        mediaAdmin = MediaAdminFactory.getMediaAdminInstance();
        mediaObservableList = FXCollections.observableArrayList();
        producersObservableList = FXCollections.observableArrayList();
        refreshMediaList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.producerColumn.setCellValueFactory(cellData -> cellData.getValue().producerProperty());
        this.addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        this.dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        this.accessCountColumn.setCellValueFactory(cellData -> cellData.getValue().accessCountProperty().asObject());
        this.mediaTableView.setItems(mediaObservableList);
        this.mediaTableView.setRowFactory(tableview -> new MediaListCell(this::onDrageEnded));
        this.mediaTableView.getSelectionModel().selectedItemProperty().addListener((component, oldValue, newValue) -> isDeleteMediaVisible.set(newValue != null));

        this.allProducersColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.uploadsCountColumn.setCellValueFactory(new PropertyValueFactory<>("uploadsCount"));
        this.producerTableView.setItems(producersObservableList);
        this.producerTableView.getSelectionModel().selectedItemProperty().addListener((component, oldValue, newValue) -> isDeleteUploaderVisible.set(newValue != null));

        this.deleteMediaButton.visibleProperty().bind(isDeleteMediaVisible);
        this.deleteUploaderButton.visibleProperty().bind(isDeleteUploaderVisible);

        typebox.valueProperty().addListener((component, oldValue, newValue) -> {
            selectedType = newValue;
            refreshMediaList();
        } );
    }

    private void onDrageEnded(MediaItemWithProperties previousMedia, MediaItemWithProperties newMedia) {
        MediaContent previousMediaContent = (MediaContent) previousMedia.getMedia();
        MediaContent newMediaContent = (MediaContent) newMedia.getMedia();
        String prevAddress = previousMediaContent.getAddress();
        String newAddress = newMediaContent.getAddress();
        previousMediaContent.setAddress(newAddress);
        newMediaContent.setAddress(prevAddress);
        updateMediaInDB(previousMediaContent);
        updateMediaInDB(newMediaContent);
    }

    public synchronized void deleteMedia(ActionEvent actionEvent) {
        String deleteCommand = deleteMediaTextField.getText();
        try {
            mediaAdmin.deleteUploaderByName(deleteCommand);
            deleteMediaTextField.setText("");
            refreshMediaList();
        } catch (IllegalArgumentException e) {
            try {
                mediaAdmin.deleteMediaByAddress(deleteCommand);
                deleteMediaTextField.setText("");
                refreshMediaList();
            } catch (IllegalArgumentException u) {
                displayError("Invalid Input");
            }
        }
    }

    public synchronized void uploadMedia(ActionEvent actionEvent) {
        String creationCommand = createMediaTextField.getText();
        handleCreateEvent(creationCommand);
    }

    public synchronized void deleteUploader() {
        ProducerAndUploadsCount producerAndUploadsCount = producerTableView.getSelectionModel().selectedItemProperty().getValue();
        if (producerAndUploadsCount != null) {
            try {
                mediaAdmin.deleteUploaderByName(producerAndUploadsCount.getName());
                refreshMediaList();
            } catch (IllegalArgumentException e) {
                displayError(e.getMessage());
            }
        }
    }

    public synchronized void deleteSelectedMedia(ActionEvent actionEvent) {
        MediaItemWithProperties mediaItemWithProperties = mediaTableView.getSelectionModel().selectedItemProperty().getValue();
        if (mediaItemWithProperties != null) {
            try {
                mediaAdmin.deleteMediaByAddress(mediaItemWithProperties.getAddress());
                refreshMediaList();
            } catch (IllegalArgumentException e) {
                displayError(e.getMessage());
            }
        }
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
                onVideoUploaded(interactiveVideo.getAddress());
            } else if (isLicensedAudioVideo(mediaType)) {
                String audioEncoding = parsedString[8];
                int samplingRate = Integer.parseInt(parsedString[9]);
                String holder = parsedString[10];
                LicensedAudioVideo licensedAudioVideo = new LicensedAudioVideoImpl(samplingRate, width, height,
                        audioEncoding, holder, bitrate, duration, producer);
                licensedAudioVideo.setTags(tags);
                mediaAdmin.upload(licensedAudioVideo);
                onVideoUploaded(licensedAudioVideo.getAddress());
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
            createMediaTextField.setText("");
            refreshMediaList();
        } catch (IllegalArgumentException e) {
            displayError(e.getMessage());
        }
    }

    private void displayError(String error) {
        new Alert(Alert.AlertType.ERROR, error).show();
    }

    private void onVideoUploaded(String address) {
        refreshMediaList();
        createMediaTextField.setText("");
    }

    private void refreshMediaList() {
        if (mediaTableView != null && mediaTableView.getSortOrder().size() > 0) {
            mediaSortColumn = (TableColumn<MediaItemWithProperties, String>) mediaTableView.getSortOrder().get(0);
            mediaSortType = mediaSortColumn.getSortType();
        }
        mediaObservableList.clear();
        mediaObservableList.addAll(getMediaBasedOnType());
        sortTableOnColumn(mediaTableView, mediaSortColumn, mediaSortType);

        if (producerTableView != null && producerTableView.getSortOrder().size() > 0) {
            producersSortColumn = (TableColumn<ProducerAndUploadsCount, String>) producerTableView.getSortOrder().get(0);
            producerSortType = producersSortColumn.getSortType();
        }
        producersObservableList.clear();
        producersObservableList.addAll(mediaAdmin.listProducersAndUploadsCount().entrySet().stream().map(e -> new ProducerAndUploadsCount(e.getKey().getName(), e.getValue())).collect(Collectors.toList()));
        sortTableOnColumn(producerTableView, producersSortColumn, producerSortType);
    }

    private List<MediaItemWithProperties> getMediaBasedOnType() {
        List<?> media = new ArrayList<>();
        if (selectedType == null || selectedType.equals("All")) {
            media = mediaAdmin.listMedia(null);
        } else if (isInteractiveVideo(selectedType)) {
            media = mediaAdmin.listMedia(InteractiveVideo.class);
        } else if (isLicensedAudioVideo(selectedType)) {
            media = mediaAdmin.listMedia(LicensedAudioVideo.class);
        }

        return media.stream().map(MediaItemWithProperties::new).collect(Collectors.toList());
    }

    private void sortTableOnColumn(TableView tableView, TableColumn column, TableColumn.SortType sortType) {
        if (column != null) {
            tableView.getSortOrder().add(column);
            column.setSortType(sortType);
            column.setSortable(true); // This performs a sort
        }
    }

    private void updateMediaInDB(MediaContent mediaContent) {
        if (mediaContent instanceof Audio) {
            Audio audio = (Audio) mediaContent;
            mediaAdmin.update(audio);
        } else if (mediaContent instanceof Video) {
            Video video = (Video) mediaContent;
            mediaAdmin.update(video);
        }
    }

}

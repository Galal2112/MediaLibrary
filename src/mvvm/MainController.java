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
import model.Producer;
import storage.InsufficientStorageException;
import storage.MediaStorage;
import util.MediaAdminFactory;
import util.MediaParser;
import util.MediaUtil;

import java.net.URL;
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
        mediaAdmin = MediaAdminFactory.getMediaAdminInstance(new MediaStorage(1024));
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
            UploadableMediaContent mediaContent = MediaParser.parseMedia(inputText);
            mediaAdmin.upload(mediaContent);
            onMediaUploaded(mediaContent.getAddress());
        } catch (IllegalArgumentException | InsufficientStorageException e) {
            displayError(e.getMessage());
        }
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

    private void onMediaUploaded(String address) {
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
        } else {
            Class<? extends UploadableMediaContent> cls = MediaUtil.getMediaClass(selectedType);
            if (cls != null) {
                media = mediaAdmin.listMedia(cls);
            }
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

package gui;

import businessLogic.MediaAdmin;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import mediaDB.Audio;
import mediaDB.MediaContent;
import mediaDB.UploadableMediaContent;
import mediaDB.Video;
import model.Producer;
import observer.Observer;
import storage.InsufficientStorageException;
import storage.MediaStorage;
import util.MediaParser;
import util.MediaUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController implements Initializable, Observer {

    @FXML private TableView<ProducerAndUploadsCount> producerTableView;
    @FXML private TableView<MediaItemWithProperties> mediaTableView;
    @FXML private TableColumn<MediaItemWithProperties, String> producerColumn;
    @FXML private TableColumn<MediaItemWithProperties, String> addressColumn;
    @FXML private TableColumn<MediaItemWithProperties, String> dateColumn;
    @FXML private TableColumn<MediaItemWithProperties, Long> accessCountColumn;
    @FXML private TableColumn<MediaItemWithProperties, String> typeColumn;
    @FXML private TextField createMediaTextField;
    @FXML private TextField deleteMediaTextField;
    @FXML private TableColumn<ProducerAndUploadsCount, String> allProducersColumn;
    @FXML private TableColumn<ProducerAndUploadsCount, String> uploadsCountColumn;
    @FXML private ComboBox<String> typebox;
    @FXML private Button deleteMediaButton;
    @FXML private Button deleteUploaderButton;
    @FXML private TextField saveMediaAddressTextField;
    @FXML private TextField loadMediaAddressTextField;
    @FXML private TextField retrivalAddressTextField;
    @FXML private PieChart sizePieChart;

    private ObservableList<MediaItemWithProperties> mediaObservableList;
    private ObservableList<ProducerAndUploadsCount> producersObservableList;
    private final MediaAdmin mediaAdmin;
    private final MediaStorage mediaStorage;
    private TableColumn<ProducerAndUploadsCount, String> producersSortColumn = null;
    private TableColumn.SortType producerSortType = null;
    private TableColumn<MediaItemWithProperties, String> mediaSortColumn = null;
    private TableColumn.SortType mediaSortType = null;
    private String selectedType;
    private SimpleBooleanProperty isDeleteMediaVisible = new SimpleBooleanProperty();
    private SimpleBooleanProperty isDeleteUploaderVisible = new SimpleBooleanProperty();
    private final PieChart.Data freeSpaceData = new PieChart.Data("Free", 100);
    private final PieChart.Data usedSpaceData = new PieChart.Data("Used", 0);

    public MainController(MediaAdmin mediaAdmin, MediaStorage mediaStorage)  {
        this.mediaAdmin = mediaAdmin;
        this.mediaStorage = mediaStorage;
        mediaAdmin.register(this);
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
        this.typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        this.mediaTableView.setItems(mediaObservableList);
        this.mediaTableView.setRowFactory(tableview -> new MediaListCell(this::onDrageEnded));
        this.mediaTableView.getSelectionModel().selectedItemProperty().addListener((component, oldValue, newValue) -> {
            isDeleteMediaVisible.set(newValue != null);
            if (newValue != null) {
                saveMediaAddressTextField.setText(newValue.getAddress());
            }
        });

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
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(freeSpaceData, usedSpaceData);
        sizePieChart.setData(pieChartData);
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
        } catch (IllegalArgumentException e) {
            try {
                mediaAdmin.deleteMediaByAddress(deleteCommand);
                deleteMediaTextField.setText("");
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
            } catch (IllegalArgumentException e) {
                displayError(e.getMessage());
            }
        }
    }

    public synchronized void loadJOS(ActionEvent actionEvent) {
        try {
            mediaAdmin.loadJOS();
            refreshMediaList();
            displaySuccess("JOS Loaded");
        } catch (IOException e) {
            displayError("File not Found, No data is saved");
        } catch (InsufficientStorageException e) {
            displayError(e.getMessage());
        }
    }

    public synchronized void saveJOS(ActionEvent actionEvent) {
        try {
            mediaAdmin.saveJOS();
            displaySuccess("JOS Saved");
        } catch (IOException e) {
            displayError(e.getMessage());
        }
    }

    public synchronized void loadJBP(ActionEvent actionEvent) {
        try {
            mediaAdmin.loadJBP();
            refreshMediaList();
            displaySuccess("JBP Loaded");
        } catch (IOException e) {
            displayError("File not Found, No data is saved");
        } catch (InsufficientStorageException e) {
            displayError(e.getMessage());
        }
    }
    public synchronized void saveJBP(ActionEvent actionEvent) {
        mediaAdmin.saveJBP();
        displaySuccess("JBP Saved");
    }

    public synchronized void loadMediaByAddress(ActionEvent actionEvent) {
        String loadAddress = loadMediaAddressTextField.getText();
        try {
            mediaAdmin.load(loadAddress);
            refreshMediaList();
        } catch (InsufficientStorageException | IllegalArgumentException e) {
            displayError(e.getMessage());
        }
    }

    public synchronized void saveMediaByAddress(ActionEvent actionEvent) {
        String saveAddress = saveMediaAddressTextField.getText();
        try {
            mediaAdmin.save(saveAddress);
            displaySuccess("Saved");
        } catch (IllegalArgumentException e) {
            displayError(e.getMessage());
        }
    }

    public synchronized void retriveMedia(ActionEvent actionEvent) {
        String address = retrivalAddressTextField.getText().trim();
        if (!address.isEmpty()) {
            Optional<MediaContent> media = mediaAdmin.retrieveMediaByAddress(address);
            if (media.isEmpty()) {
                displayError("Not found");
            } else {
                retrivalAddressTextField.setText("");
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
        } catch (IllegalArgumentException e) {
            displayError(e.getMessage());
        }
    }

    private void displayError(String error) {
        new Alert(Alert.AlertType.ERROR, error).show();
    }

    private void displaySuccess(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).show();
    }

    private void onMediaUploaded(String address) {
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

        BigDecimal freeSpace = mediaStorage.getAvailableMediaStorageInMB();
        BigDecimal totalSpace = mediaStorage.getDiskSize();
        float freeSpacePercent = freeSpace.divide(totalSpace).floatValue() * 100;
        freeSpaceData.setPieValue(freeSpacePercent);
        usedSpaceData.setPieValue(100 - freeSpacePercent);
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

    @Override
    public void updateObserver() {
        refreshMediaList();
    }
}

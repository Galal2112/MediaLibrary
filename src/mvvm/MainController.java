package mvvm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import mediaDB.Video;
import util.RandomGenerator;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    @FXML private TableView<MediaItemWithProperties> tableview;
    @FXML private TableColumn<MediaItemWithProperties, String> producerColumn;
    @FXML private TableColumn<MediaItemWithProperties, String> addressColumn;
    @FXML private TableColumn<MediaItemWithProperties, String> dateColumn;
    @FXML private TableColumn<MediaItemWithProperties, Long> accessCountColumn;

    private ObservableList<MediaItemWithProperties> mediaObservableList;

    public MainController()  {
        mediaObservableList = FXCollections.observableArrayList();

        ArrayList<Video> randomVideos = new ArrayList<>();
        for (int i = 0; i < 20; i ++) {
            Video video = RandomGenerator.getRandomMedia();
            video.setUploadDate(new Date());
            video.setAddress("Address " + i);
            video.setAccessCount(i);
            randomVideos.add(video);
        }
        mediaObservableList.addAll(randomVideos.stream().map(MediaItemWithProperties::new).collect(Collectors.toList()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.producerColumn.setCellValueFactory(cellData -> cellData.getValue().producerProperty());
        this.addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        this.dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        this.accessCountColumn.setCellValueFactory(cellData -> cellData.getValue().accessCountProperty().asObject());
        this.tableview.setItems(mediaObservableList);
    }
}

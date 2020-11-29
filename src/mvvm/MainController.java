package mvvm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import mediaDB.Video;
import util.RandomGenerator;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController implements Initializable {
    @FXML private ListView<MediaItemWithProperties> listView;
    private ObservableList<MediaItemWithProperties> mediaObservableList;

    public MainController()  {

        mediaObservableList = FXCollections.observableArrayList();

        ArrayList<Video> randomVideos = new ArrayList<>();
        for (int i = 0; i < 10; i ++) {
            Video video = RandomGenerator.getRandomMedia();
            video.setUploadDate(new Date());
            video.setAddress("Address " + i);
            randomVideos.add(video);
        }
        mediaObservableList.addAll(randomVideos.stream().map(MediaItemWithProperties::new).collect(Collectors.toList()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.listView.setItems(mediaObservableList);
        this.listView.setCellFactory(mediaListView -> new MediaListCell());
    }
}

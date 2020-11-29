package mvvm;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class MediaListCell extends ListCell<MediaItemWithProperties> {

    private FXMLLoader mLLoader;
    @FXML
    private Label producer;
    @FXML
    private Label address;
    @FXML
    private Label date;
    @FXML
    private Label accessCount;
    @FXML
    private HBox hbox;

    @Override
    protected void updateItem(MediaItemWithProperties item, boolean empty) {
        super.updateItem(item, empty);

        if(empty || item == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        if (mLLoader == null) {
            mLLoader = new FXMLLoader(getClass().getResource("media_cell.fxml"));
            mLLoader.setController(this);

            try {
                mLLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        producer.textProperty().bind(item.producerProperty());
        date.textProperty().bind(item.dateProperty());
        accessCount.textProperty().bind(item.accessCountProperty().asString());
        address.textProperty().bind(item.addressProperty());

        setText(null);
        setGraphic(hbox);
    }
}

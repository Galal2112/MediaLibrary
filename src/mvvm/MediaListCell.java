package mvvm;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableRow;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.util.ArrayList;
import java.util.List;

public class MediaListCell extends TableRow<MediaItemWithProperties> {

    public MediaListCell() {
        TableRow thisCell = this;

        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setAlignment(Pos.CENTER);

        setOnDragDetected(event -> {
            if (getItem() == null) {
                return;
            }

            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(getItem().getAddress());

            MediaListCell source = (MediaListCell) event.getSource();
            dragboard.setDragView(source.snapshot(new SnapshotParameters(), null));
            dragboard.setContent(content);

            event.consume();
        });

        setOnDragOver(event -> {
            if (event.getGestureSource() != thisCell &&
                    event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }

            event.consume();
        });

        setOnDragEntered(event -> {
            if (event.getGestureSource() != thisCell &&
                    event.getDragboard().hasString()) {
                setOpacity(0.3);
            }
        });

        setOnDragExited(event -> {
            if (event.getGestureSource() != thisCell &&
                    event.getDragboard().hasString()) {
                setOpacity(1);
            }
        });

        setOnDragDropped(event -> {
            if (getItem() == null) {
                return;
            }

            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                ObservableList<MediaItemWithProperties> items = getTableView().getItems();
                MediaItemWithProperties previousItem = items.stream().filter(item -> item.getAddress()
                        .equals(db.getString())).findFirst().get();
                MediaItemWithProperties newItem = getItem();
                int draggedIdx = items.indexOf(previousItem);
                int thisIdx = items.indexOf(newItem);
                items.set(draggedIdx, newItem);
                items.set(thisIdx, previousItem);
                String tempAddress = previousItem.getAddress();
                previousItem.setAddress(newItem.getAddress());
                newItem.setAddress(tempAddress);
                List<MediaItemWithProperties> itemscopy = new ArrayList<>(getTableView().getItems());
                getTableView().getItems().setAll(itemscopy);

                success = true;
            }
            event.setDropCompleted(success);

            event.consume();
        });

        setOnDragDone(DragEvent::consume);
    }
}

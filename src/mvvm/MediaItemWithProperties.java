package mvvm;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import mediaDB.MediaContent;
import mediaDB.Uploadable;

public final class MediaItemWithProperties {

    private final StringProperty producerProperty = new SimpleStringProperty();
    private final StringProperty addressProperty = new SimpleStringProperty();
    private final StringProperty dateProperty = new SimpleStringProperty();
    private final SimpleLongProperty accessCountProperty = new SimpleLongProperty();

    public MediaItemWithProperties(MediaContent media) {
        if (media instanceof Uploadable) {
            Uploadable uploadable = (Uploadable) media;
            this.producerProperty.set(uploadable.getUploader().getName());
            this.dateProperty.set(uploadable.getUploadDate().toString());
        } else {
            this.producerProperty.set("undefined");
            this.dateProperty.set("undefined");
        }
        this.addressProperty.set(media.getAddress());
    }

    public StringProperty producerProperty() {return this.producerProperty;}
    public StringProperty addressProperty() {return this.addressProperty;}
    public StringProperty dateProperty() {return this.dateProperty;}
    public SimpleLongProperty accessCountProperty() {return this.accessCountProperty;}

    public String getProducer() {
        return this.producerProperty.get();
    }
    public void setProducer(String value){
        this.producerProperty.set(value);
    }
    public String getAddress() {
        return this.addressProperty.get();
    }
    public void setAddress(String value){
        this.addressProperty.set(value);
    }
    public String getDate() {
        return this.dateProperty.get();
    }
    public void setDate(String value){
        this.dateProperty.set(value);
    }
    public Long getAccessCount() {
        return this.accessCountProperty.get();
    }
    public void setAccessCount(Long value){
        this.accessCountProperty.set(value);
    }
}

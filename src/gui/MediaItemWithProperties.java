package gui;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import mediaDB.Audio;
import mediaDB.MediaContent;
import mediaDB.Uploadable;
import mediaDB.Video;

public final class MediaItemWithProperties {

    private final StringProperty producerProperty = new SimpleStringProperty();
    private final StringProperty typeProperty = new SimpleStringProperty();
    private final StringProperty addressProperty = new SimpleStringProperty();
    private final StringProperty dateProperty = new SimpleStringProperty();
    private final SimpleLongProperty accessCountProperty = new SimpleLongProperty();
    private Object media;

    public MediaItemWithProperties(Object media) {
        this.media = media;
        this.typeProperty.set(media.getClass().getSimpleName());
        if (media instanceof Audio) {
            bindData((Audio) media);
        } else if (media instanceof Video) {
            bindData((Video) media);
        } else {
            this.producerProperty.set("undefined");
            this.typeProperty.set("undefined");
            this.dateProperty.set("undefined");
            this.addressProperty.set("undefined");
            this.accessCountProperty.set(0);
        }
    }

    public Object getMedia() {
        return media;
    }

    public StringProperty producerProperty() {return this.producerProperty;}
    public StringProperty typeProperty() {return this.typeProperty;}
    public StringProperty addressProperty() {return this.addressProperty;}
    public StringProperty dateProperty() {return this.dateProperty;}
    public SimpleLongProperty accessCountProperty() {return this.accessCountProperty;}

    public String getProducer() {
        return this.producerProperty.get();
    }
    public String getType() {
        return this.typeProperty.get();
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

    private <T extends MediaContent & Uploadable> void bindData(T media) {
        this.producerProperty.set(media.getUploader().getName());
        this.dateProperty.set(media.getUploadDate().toString());
        this.addressProperty.set(media.getAddress());
        this.accessCountProperty.set(media.getAccessCount());
    }
}

package model;

import mediaDB.InteractiveVideo;
import mediaDB.Tag;
import mediaDB.Uploader;

import java.util.Collection;

public class InteractiveVideoImpl extends VideoImpl implements InteractiveVideo {

    private String type;

    public InteractiveVideoImpl() {
        super();
    }

    public InteractiveVideoImpl(String type, int width, int height, String encoding,
                                long bitrate, long length, Uploader uploader, Collection<Tag> tags) {
        super(width, height, encoding, bitrate, length, uploader, tags);
        this.type = type;
    }


    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public InteractiveVideo copy() {
        InteractiveVideoImpl interactiveVideo = new InteractiveVideoImpl(type, getWidth(), getHeight(), getEncoding(),
                getBitrate(), getLength(), getUploader(), getTags());
        interactiveVideo.setAccessCount(getAccessCount());
        interactiveVideo.setAddress(getAddress());
        interactiveVideo.setUploadDate(getUploadDate());
        return interactiveVideo;
    }
}

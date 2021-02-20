package model;

import mediaDB.AudioVideo;
import mediaDB.Tag;
import mediaDB.Uploader;

import java.util.Collection;

public class AudioVideoImpl extends AudioImpl implements AudioVideo {

    private int width;
    private int height;

    public AudioVideoImpl() {
        super();
    }

    public AudioVideoImpl(int width, int height,
                          int samplingRate, String encoding, Long bitrate, long length,
                          Uploader uploader, Collection<Tag> tags) {
        super(samplingRate, encoding, bitrate, length, uploader, tags);
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public AudioVideo copy() {
        AudioVideoImpl audioVideo = new AudioVideoImpl(width, height, getSamplingRate(),
                getEncoding(), getBitrate(), getLength(), getUploader(), getTags());
        audioVideo.setAccessCount(getAccessCount());
        audioVideo.setAddress(getAddress());
        audioVideo.setUploadDate(getUploadDate());
        return audioVideo;
    }
}

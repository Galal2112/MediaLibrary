package model;

import mediaDB.LicensedVideo;
import mediaDB.Tag;
import mediaDB.Uploader;

import java.util.Collection;

public class LicensedVideoImpl extends VideoImpl implements LicensedVideo {

    private String holder;

    public LicensedVideoImpl() {}

    public LicensedVideoImpl(String holder, int width, int height, String encoding, long bitrate, long length, Uploader uploader, Collection<Tag> tags) {
        super(width, height, encoding, bitrate, length, uploader, tags);
        this.holder = holder;
    }

    @Override
    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    @Override
    public LicensedVideo copy() {
        LicensedVideoImpl video = new LicensedVideoImpl(getHolder(), getWidth(), getHeight(), getEncoding(), getBitrate(), getLength(), getUploader(), getTags());
        video.setAccessCount(getAccessCount());
        video.setAddress(getAddress());
        video.setUploadDate(getUploadDate());
        return video;
    }
}

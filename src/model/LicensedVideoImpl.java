package model;

import mediaDB.LicensedVideo;
import mediaDB.Tag;
import mediaDB.Uploader;

import java.time.Duration;
import java.util.Collection;

public class LicensedVideoImpl extends VideoImpl implements LicensedVideo {

    private String holder;

    public LicensedVideoImpl(String holder, int width, int height, String encoding, long bitrate, Duration length, Uploader uploader, Collection<Tag> tags) {
        super(width, height, encoding, bitrate, length, uploader, tags);
        this.holder = holder;
    }

    @Override
    public String getHolder() {
        return holder;
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

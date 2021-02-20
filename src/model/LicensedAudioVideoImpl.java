package model;

import mediaDB.LicensedAudioVideo;
import mediaDB.Tag;
import mediaDB.Uploader;

import java.util.Collection;

public class LicensedAudioVideoImpl extends AudioVideoImpl implements LicensedAudioVideo {

    private String holder;

    public LicensedAudioVideoImpl() {
        super();
    }

    public LicensedAudioVideoImpl(int width, int height,
                                  int samplingRate, String encoding,
                                  String holder, Long bitrate, long length,
                                  Uploader uploader, Collection<Tag> tags) {
        super(width, height, samplingRate, encoding, bitrate, length, uploader, tags);
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
    public LicensedAudioVideo copy() {
        LicensedAudioVideoImpl licensedAudioVideo = new LicensedAudioVideoImpl(getWidth(), getHeight(), getSamplingRate(),
                getEncoding(), holder, getBitrate(), getLength(), getUploader(), getTags());
        licensedAudioVideo.setAccessCount(getAccessCount());
        licensedAudioVideo.setAddress(getAddress());
        licensedAudioVideo.setUploadDate(getUploadDate());
        return licensedAudioVideo;
    }
}

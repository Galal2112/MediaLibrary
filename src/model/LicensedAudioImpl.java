package model;

import mediaDB.LicensedAudio;
import mediaDB.Tag;
import mediaDB.Uploader;

import java.util.Collection;

public class LicensedAudioImpl extends AudioImpl implements LicensedAudio {

    private String holder;

    public LicensedAudioImpl() {}

    public LicensedAudioImpl(String holder, int samplingRate, String encoding, Long bitrate, long length, Uploader uploader, Collection<Tag> tags) {
        super(samplingRate, encoding, bitrate, length, uploader, tags);
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
    public LicensedAudio copy() {
        LicensedAudioImpl licensedAudio = new LicensedAudioImpl(holder, getSamplingRate(),
                getEncoding(), getBitrate(), getLength(), getUploader(), getTags());
        licensedAudio.setAccessCount(getAccessCount());
        licensedAudio.setAddress(getAddress());
        licensedAudio.setUploadDate(getUploadDate());
        return licensedAudio;
    }
}

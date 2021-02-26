package model;

import mediaDB.Audio;
import mediaDB.Tag;
import mediaDB.Uploader;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

public class AudioImpl implements Audio {
    private int samplingRate;
    private String encoding;
    private long bitrate;
    private long length;
    private BigDecimal size;
    private String address;
    private Collection<Tag> tags;
    private long accessCount;
    private Uploader uploader;
    private Date uploadDate;

    public AudioImpl() {}

    public AudioImpl(int samplingRate, String encoding, long bitrate, long length,
                                  Uploader uploader, Collection<Tag> tags) {
        this.samplingRate = samplingRate;
        this.encoding = encoding;
        this.bitrate = bitrate;
        this.length = length;
        this.size = MediaSizeUtil.getMediaSize(bitrate, length);
        this.uploader = uploader;
        this.tags = tags;
    }


    @Override
    public int getSamplingRate() {
        return samplingRate;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }


    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public long getBitrate() {
        return bitrate;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public BigDecimal getSize() {
        return size;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public Collection<Tag> getTags() {
        return tags;
    }

    @Override
    public void setTags(Collection<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public long getAccessCount() {
        return accessCount;
    }

    @Override
    public void setAccessCount(long accessCount) {
        this.accessCount = accessCount;
    }

    @Override
    public Uploader getUploader() {
        return uploader;
    }

    @Override
    public Date getUploadDate() {
        return uploadDate;
    }

    @Override
    public void setUploadDate(Date date) {
        this.uploadDate = date;
    }

    public void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }

    public void setBitrate(long bitrate) {
        this.bitrate = bitrate;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setSize(BigDecimal size) {
        this.size = size;
    }

    public void setUploader(Uploader uploader) {
        this.uploader = uploader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AudioImpl audio = (AudioImpl) o;
        return address.equals(audio.address);
    }

    @Override
    public Audio copy() {
        AudioImpl audio = new AudioImpl(samplingRate, encoding, bitrate, length, uploader, tags);
        audio.accessCount = accessCount;
        audio.address = address;
        audio.uploadDate = uploadDate;
        return audio;
    }
}

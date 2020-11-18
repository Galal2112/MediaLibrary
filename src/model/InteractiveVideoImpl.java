package model;

import mediaDB.InteractiveVideo;
import mediaDB.Tag;
import mediaDB.Uploader;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;

public class InteractiveVideoImpl implements InteractiveVideo {

    private String type;
    private int width;
    private int height;
    private String encoding;
    private long bitrate;
    private Duration length;
    private BigDecimal size;
    private String address;
    private Collection<Tag> tags;
    private long accessCount;
    private Uploader uploader;
    private Date uploadDate;

    public InteractiveVideoImpl(String type, int width, int height, String encoding,
                                long bitrate, Duration length, Uploader uploader) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.encoding = encoding;
        this.bitrate = bitrate;
        this.length = length;
        this.size = MedisSizeUtil.getMediaSize(bitrate, length);
        this.uploader = uploader;
    }


    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public long getBitrate() {
        return bitrate;
    }

    @Override
    public Duration getLength() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InteractiveVideoImpl interactiveVideo = (InteractiveVideoImpl) o;
        return address.equals(interactiveVideo.address);
    }
}

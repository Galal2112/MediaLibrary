package model;

import mediaDB.Tag;
import mediaDB.Uploader;
import mediaDB.Video;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

public class VideoImpl implements Video {
    private int width;
    private int height;
    private String encoding;
    private long bitrate;
    private long length;
    private BigDecimal size;
    private String address;
    private Collection<Tag> tags;
    private long accessCount;
    private Uploader uploader;
    private Date uploadDate;

    public VideoImpl() {}

    public VideoImpl(int width, int height, String encoding,
                                long bitrate, long length, Uploader uploader, Collection<Tag> tags) {
        this.width = width;
        this.height = height;
        this.encoding = encoding;
        this.bitrate = bitrate;
        this.length = length;
        this.size = MediaSizeUtil.getMediaSize(bitrate, length);
        this.uploader = uploader;
        this.tags = tags;
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

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
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
        VideoImpl video = (VideoImpl) o;
        return address.equals(video.address);
    }

    @Override
    public Video copy() {
        VideoImpl video = new VideoImpl(width, height, encoding, bitrate, length, uploader, tags);
        video.accessCount = accessCount;
        video.address = address;
        video.uploadDate = uploadDate;
        return video;
    }
}

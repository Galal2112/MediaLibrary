package model;

import mediaDB.MediaContent;

import java.math.BigDecimal;
import java.util.HashMap;

public class MediaStorge {


    private BigDecimal availableMediaStorageInMB;
    private final HashMap<String, MediaContent> hardDisk = new HashMap<>();

    public static MediaStorge sharedInstance = new MediaStorge();


    private MediaStorge() {
        //10 TB
        availableMediaStorageInMB = BigDecimal.valueOf(1024.0 * 1024.0 * 10);
    }

    public void addMediaInStorage(MediaContent mediaContent) {
        //check size
        if (availableMediaStorageInMB.compareTo(mediaContent.getSize()) < 0) {
            throw new IllegalArgumentException("Insufficient Storage");
        } else {
            hardDisk.put(mediaContent.getAddress(), mediaContent);
            availableMediaStorageInMB = availableMediaStorageInMB.subtract(mediaContent.getSize());
        }
    }

    public void deletedMediaFromStorage(MediaContent mediaContent) {
        hardDisk.remove(mediaContent.getAddress());
        availableMediaStorageInMB = availableMediaStorageInMB.add(mediaContent.getSize());
    }

    public void deleteMediaByAddress(String address) throws IllegalArgumentException {
        MediaContent media = hardDisk.get(address);
        if(media == null){
            throw new IllegalArgumentException("Invalid address");
        }
        deletedMediaFromStorage(media);
    }

    public BigDecimal getAvailableMediaStorageInMB() {
        return availableMediaStorageInMB;
    }


}

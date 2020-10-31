package model;

import mediaDB.MediaContent;
import observer.Observer;
import observer.Subject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MediaStorge implements Subject {

    private final List<Observer> observerList = new LinkedList<>();

    private final BigDecimal diskSize = BigDecimal.valueOf(1024.0 * 1024.0 * 10);

    private BigDecimal availableMediaStorageInMB;
    private final HashMap<String, MediaContent> hardDisk = new HashMap<>();

    public static MediaStorge sharedInstance = new MediaStorge();


    private MediaStorge() {
        //10 TB
        availableMediaStorageInMB = diskSize;
    }

    public void addMediaInStorage(MediaContent mediaContent) {
        //check size
        if (availableMediaStorageInMB.compareTo(mediaContent.getSize()) < 0) {
            throw new IllegalArgumentException("Insufficient Storage");
        } else {
            hardDisk.put(mediaContent.getAddress(), mediaContent);
            availableMediaStorageInMB = availableMediaStorageInMB.subtract(mediaContent.getSize());
            benachrichtige();
        }
    }

    public void deletedMediaFromStorage(MediaContent mediaContent) {
        hardDisk.remove(mediaContent.getAddress());
        availableMediaStorageInMB = availableMediaStorageInMB.add(mediaContent.getSize());
        benachrichtige();
    }

    public void deleteMediaByAddress(String address) throws IllegalArgumentException {
        MediaContent media = hardDisk.get(address);
        if(media == null) {
            throw new IllegalArgumentException("Invalid address");
        }
        deletedMediaFromStorage(media);
    }

    public BigDecimal getAvailableMediaStorageInMB() {
        return availableMediaStorageInMB;
    }

    public BigDecimal getDiskSize() {
        return diskSize;
    }

    @Override
    public void register(Observer observer) {
        observerList.add(observer);
    }

    @Override
    public void unsubscribe(Observer observer) {
        observerList.remove(observer);
    }

    @Override
    public void benachrichtige() {
        for (Observer observer : observerList) {
            observer.updateObserver();
        }
    }
}

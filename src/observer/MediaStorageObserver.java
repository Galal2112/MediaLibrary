package observer;

import storage.MediaStorage;

import java.math.BigDecimal;

public class MediaStorageObserver implements Observer {

    private MediaStorage mediaStorage;

    public MediaStorageObserver(MediaStorage mediaStorage) {
        this.mediaStorage = mediaStorage;
    }

    @Override
    public void updateObserver() {
        BigDecimal hardTotalSize = mediaStorage.getDiskSize();
        BigDecimal freeSize = mediaStorage.getAvailableMediaStorageInMB();
        float freeSizePercent = freeSize.divide(hardTotalSize).floatValue() * 100;
        if (freeSizePercent <= 10) {
            System.out.println("\u26A0 Warning: " + (100 - freeSizePercent) + "% of Storage is Used ");
        }
    }
}

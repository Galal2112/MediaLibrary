package observer;

import model.MediaStorge;

import java.math.BigDecimal;

public class MediaStorageObserver implements Observer {

    private MediaStorge mediaStorge;

    public MediaStorageObserver(MediaStorge mediaStorge) {
        this.mediaStorge = mediaStorge;
        this.mediaStorge.register(this);
    }

    @Override
    public void updateObserver() {
        BigDecimal hardTotalSize = mediaStorge.getDiskSize();
        BigDecimal freeSize = mediaStorge.getAvailableMediaStorageInMB();
        float freeSizePercent = freeSize.divide(hardTotalSize).floatValue() * 100;
        if (freeSizePercent <= 10) {
            System.out.println("\u26A0 Warning: " + (100 - freeSizePercent) + "% of Storage is Used ");
        }
    }
}

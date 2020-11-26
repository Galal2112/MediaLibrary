package storage;

import mediaDB.MediaContent;
import observer.Observer;
import observer.Subject;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MediaStorage implements Subject {


    private final Lock lock = new ReentrantLock();
    // what is difference between deque and queue? TODO
    private final ConcurrentLinkedQueue<Observer> observerList = new ConcurrentLinkedQueue<>();

    //10 TB
    private final BigDecimal diskSize = BigDecimal.valueOf(1024.0 * 1024.0 * 10);

    private BigDecimal availableMediaStorageInMB;

    public final static MediaStorage sharedInstance = new MediaStorage();


    private MediaStorage() {
        //10 TB
        availableMediaStorageInMB = diskSize;
    }

    public void addMediaInStorage(MediaContent mediaContent) throws InsufficientStorageException {
        //Anfang des Kritischen Bereichs;
        this.lock.lock();
        try {
            // check sufficient Storage
            if (availableMediaStorageInMB.compareTo(mediaContent.getSize()) < 0) {
                throw new InsufficientStorageException();
            } else {
                availableMediaStorageInMB = availableMediaStorageInMB.subtract(mediaContent.getSize());
                notifyObserver();
            }
        } finally {
            this.lock.unlock(); // end the critical area
        }
    }

    public void deletedMediaFromStorage(MediaContent mediaContent) {
        this.lock.lock();
        try {
            availableMediaStorageInMB = availableMediaStorageInMB.add(mediaContent.getSize());
            notifyObserver();
        } finally {
            this.lock.unlock();
        }
    }

    public BigDecimal getAvailableMediaStorageInMB() {
        this.lock.lock();
        try {
            return availableMediaStorageInMB;

        } finally {
            this.lock.unlock();
        }
    }

    public BigDecimal getDiskSize() {
        return diskSize;
    }

    @Override
    public void register(Observer observer) {
        if (observer == null) return;
        observerList.add(observer);
    }

    @Override
    public void unregister(Observer observer) {
        if (observer == null) return;
        observerList.remove(observer);
    }

    @Override
    public void notifyObserver() {
        for (Observer observer : observerList) {
            observer.updateObserver();
        }
    }
}
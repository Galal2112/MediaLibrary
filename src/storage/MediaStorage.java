package storage;

import mediaDB.MediaContent;
import observer.Observer;
import observer.Subject;

import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MediaStorage implements Subject {

    private final Lock lock = new ReentrantLock();
    private final Set<Observer> observerList = ConcurrentHashMap.newKeySet();;

    private final BigDecimal diskSize;
    private BigDecimal availableMediaStorageInMB;

    public MediaStorage(long diskSize) {
        this.diskSize = BigDecimal.valueOf(diskSize);
        availableMediaStorageInMB = this.diskSize;
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

    public void clear() {
        this.lock.lock();
        try {
            availableMediaStorageInMB = diskSize;
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

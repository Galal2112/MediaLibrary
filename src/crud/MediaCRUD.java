package crud;

import mediaDB.MediaContent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MediaCRUD implements CRUD<MediaContent> {
    private final Lock lock = new ReentrantLock();
    private static final LinkedList<MediaContent> mediaList = new LinkedList<>();

    @Override
    public List<MediaContent> getAll() {
        this.lock.lock();

        try {
            LinkedList<MediaContent> resultList = new LinkedList<>();
            mediaList.forEach((media) -> resultList.add((MediaContent) media.copy()));
            return resultList;

        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void create(MediaContent media) {

        this.lock.lock();

        try {
            mediaList.add((MediaContent) media.copy());
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void update(MediaContent media) {
        this.lock.lock();

        try {
            Iterator<MediaContent> it = mediaList.iterator();
            int index = 0;
            while (it.hasNext()) {
                if (it.next().getAddress().equals(media.getAddress())) {
                    mediaList.set(index, (MediaContent) media.copy());
                    break;
                }
                index++;
            }
        } finally {
            this.lock.unlock();
        }

    }

    @Override
    public Optional<MediaContent> get(String address) {
        this.lock.lock();

        try {
            Optional<MediaContent> mediaContentOptional = mediaList.stream().filter(v -> v.getAddress().equals(address)).findFirst();
            return mediaContentOptional.map(mediaContent -> (MediaContent) mediaContent.copy());
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void delete(MediaContent media) {
        deleteById(media.getAddress());
    }

    @Override
    public void deleteById(String address) {
        this.lock.lock();

        try {
            mediaList.removeIf(v -> v.getAddress().equals(address));

        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void drop() {
        mediaList.clear();
    }
}

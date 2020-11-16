package crud;

import mediaDB.Uploader;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UploaderCRUD implements CRUD<Uploader> {

    //  Lock statt Monitor .... what is Monitor??
    private final Lock lock = new ReentrantLock();

    private static final LinkedList<Uploader> uploaders = new LinkedList<>();

    //get uploader by Name
    public Optional<Uploader> get(String name) {
        this.lock.lock();
        try {
            return uploaders.stream().filter(uploader -> uploader.getName().equals(name)).findFirst();

        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public List<Uploader> getAll() {
        this.lock.lock();
        try {
            return new LinkedList<>(uploaders);

        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void create(Uploader uploader) {
        this.lock.lock();
        try {
            uploaders.add(uploader);

        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void update(Uploader uploader) {
        this.lock.lock();
        try {
            Iterator<Uploader> it = uploaders.iterator();
            int index = 0;
            while (it.hasNext()) {
                if (it.next().getName().equals(uploader.getName())) {
                    uploaders.set(index, uploader);
                    break;
                }
                index++;
            }
        } finally {
            this.lock.unlock();
        }

    }

    @Override
    public void delete(Uploader uploader) {
        deleteById(uploader.getName());

    }

    public void deleteById(String name) {

        this.lock.lock();
        try {
            uploaders.removeIf(u -> u.getName().equals(name));

        } finally {
            this.lock.unlock();
        }
    }
}

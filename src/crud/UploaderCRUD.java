package crud;

import businessLogic.PresistencyHelper;
import mediaDB.Uploader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UploaderCRUD implements CRUD<Uploader> {
    private static final String serUploadersFileName = "uploaders.ser";
    private static final String xmlUploadersFileName = "uploaders.xml";

    //  Lock statt Monitor .... what is Monitor??
    private final Lock lock = new ReentrantLock();

    private static final LinkedList<Uploader> uploaders = new LinkedList<>();

    //get uploader by Name
    public Optional<Uploader> get(String name) {
        this.lock.lock();
        try {
            Optional<Uploader> uploaderOptional = uploaders.stream().filter(uploader -> uploader.getName().equals(name)).findFirst();
            return uploaderOptional.map(Uploader::copy);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public List<Uploader> getAll() {
        this.lock.lock();
        try {
            LinkedList<Uploader> resultList = new LinkedList<>();
            uploaders.forEach((uploader) -> resultList.add(uploader.copy()));
            return resultList;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void create(Uploader uploader) {
        this.lock.lock();
        try {
            uploaders.add(uploader.copy());

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
                    uploaders.set(index, uploader.copy());
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

    @Override
    public void saveJOS() throws IOException, FileNotFoundException {
        PresistencyHelper.saveJOS(uploaders, serUploadersFileName);
    }

    @Override
    public void loadJOS() throws IOException, ClassNotFoundException {
        uploaders.clear();
        try {
            uploaders.addAll(PresistencyHelper.loadJOS(serUploadersFileName));
        } catch (FileNotFoundException e) {

        }
    }

    @Override
    public void saveJBP() throws IOException, FileNotFoundException {
        PresistencyHelper.saveUploadersUsingJBP(xmlUploadersFileName, uploaders);
    }

    @Override
    public void loadJBP() throws IOException, ClassNotFoundException, FileNotFoundException {
        uploaders.clear();
        uploaders.addAll(PresistencyHelper.loadUploaderUsingJBP(xmlUploadersFileName));
    }
}

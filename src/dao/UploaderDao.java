package dao;

import mediaDB.Uploader;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class UploaderDao implements Dao<Uploader> {

    private static final LinkedList<Uploader> uploaders = new LinkedList<>();

    public Optional<Uploader> get(String name) {
        return uploaders.stream().filter(Uploader -> Uploader.getName().equals(name)).findFirst();
    }

    @Override
    public List<Uploader> getAll() {
        return uploaders;
    }

    @Override
    public void create(Uploader uploader) {
        uploaders.add(uploader);
    }

    @Override
    public void update(Uploader uploader) {
        Iterator<Uploader> it = uploaders.iterator();
        int index = 0;
        while (it.hasNext()) {
            if (it.next().getName().equals(uploader.getName())) {
                uploaders.set(index, uploader);
                break;
            }
            index++;
        }
    }

    @Override
    public void delete(Uploader uploader) {
        uploaders.removeIf(u -> u.getName() == uploader.getName());
    }
}

package crud;

import mediaDB.Uploader;

import java.util.*;

public class UploaderCRUD implements CRUD<Uploader> {

    private static final LinkedList<Uploader> uploaders = new LinkedList<>();

    public Optional<Uploader> get(String name) {
        return uploaders.stream().filter(Uploader -> Uploader.getName().equals(name)).findFirst();
    }

    @Override
    public List<Uploader> getAll() {
        return new LinkedList<>(uploaders);
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
        deleteById(uploader.getName());
    }

    public void deleteById(String name) {
        uploaders.removeIf(u -> u.getName().equals(name));
    }
}

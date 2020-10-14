package crud;

import mediaDB.MediaContent;
import mediaDB.Uploadable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class MediaCRUD<T extends MediaContent & Uploadable> implements CRUD<T> {

    private MediaDatabase database = MediaDatabase.current;
    private Class<T> type;

    public MediaCRUD(Class<T> type) {
        this.type = type;
    }

    @Override
    public List<T> getAll() {
        return new LinkedList<>(database.getAll(type));
    }

    @Override
    public void create(T media) {
        database.insert(media);
    }

    @Override
    public void update(T media) {
       database.update(media);
    }

    @Override
    public Optional<T> get(String address) {
        return getAll().stream().filter(v -> v.getAddress().equals(address)).findFirst();
    }

    @Override
    public void delete(T media) {
        getAll().removeIf( v -> v.getAddress().equals(media.getAddress()));
    }
    @Override
    public void deleteById(String address) {
        getAll().removeIf(v -> v.getAddress().equals(address));
    }

}

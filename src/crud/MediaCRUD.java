package crud;

import mediaDB.MediaContent;
import mediaDB.Uploadable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

abstract class MediaCRUD<T extends MediaContent & Uploadable> implements CRUD<T> {

    protected abstract List<T> getList();

    @Override
    public List<T> getAll() {
        return new LinkedList<>(getList());
    }

    @Override
    public void create(T media) {
        getList().add(media);
    }

    @Override
    public void update(T media) {
        Iterator<T> it = getList().iterator();
        int index = 0;
        while (it.hasNext()) {
            if (it.next().getAddress().equals(media.getAddress())) {
                getList().set(index, media);
                break;
            }
            index ++;
        }
    }

    @Override
    public Optional<T> get(String address) {
        return getList().stream().filter(v -> v.getAddress().equals(address)).findFirst();
    }

    @Override
    public void delete(T media) {
        getList().removeIf( v -> v.getAddress().equals(media.getAddress()));
    }
    @Override
    public void deleteById(String address) {
        getList().removeIf(v -> v.getAddress().equals(address));
    }
}

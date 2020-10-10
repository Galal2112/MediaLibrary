package dao;

import mediaDB.Uploader;
import model.Producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ProducerDao implements Dao<Producer> {

    private static final ArrayList<Producer> producers = new ArrayList<>();

    public Optional<Producer> get(String name) {
        return producers.stream().filter(producer -> producer.getName().equals(name)).findFirst();
    }

    @Override
    public List<Producer> getAll() {
        return producers;
    }

    @Override
    public void create(Producer producer) {
        if (!producers.contains(producer)) {
            producers.add(producer);
        } else {
            throw new IllegalArgumentException("Username is taken");
        }
    }

    @Override
    public void update(Producer producer) {
        int index = producers.indexOf(producer);
        producers.set(index, producer);
    }

    @Override
    public void delete(Producer producer) {
        producers.remove(producer);
    }
}

package dao;

import java.util.List;

public interface Dao<T> {
    
    List<T> getAll();

    void create(T t);

    void update(T t);

    void delete(T t);
}

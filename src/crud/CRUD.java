package crud;

import java.util.List;

public interface CRUD<T> {
    
    List<T> getAll();

    void create(T t);

    void update(T t);

    void delete(T t);
}

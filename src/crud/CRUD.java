package crud;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CRUD<T> {
    
    List<T> getAll();

    void create(T t);

    void update(T t);

    void delete(T t);

   // get by ID
    Optional<T> get(String id);

    void deleteById(String id);

    void saveJOS() throws IOException, FileNotFoundException;

    void loadJOS() throws IOException, ClassNotFoundException;

    void saveJBP() throws IOException, FileNotFoundException;

    void loadJBP()throws IOException, ClassNotFoundException, FileNotFoundException ;
}

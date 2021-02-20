package mediaDB;

import java.io.Serializable;

public interface Uploader extends Serializable {
    String getName();
    void setName(String name);
    Uploader copy();
}

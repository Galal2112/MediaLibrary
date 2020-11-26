package mediaDB;

public interface Uploader {
    String getName();
    void setName(String name);
    Uploader copy();
}

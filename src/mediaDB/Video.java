package mediaDB;

public interface Video extends MediaContent,Uploadable{
    int getWidth();
    int getHeight();
    String getEncoding();
}



package mediaDB;

public interface Audio extends UploadableMediaContent {
    int getSamplingRate();
    String getEncoding();
}

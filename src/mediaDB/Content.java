package mediaDB;

import java.util.Collection;

public interface Content {
    String getAddress();
    void setAddress(String address);
    Collection<Tag> getTags();
    long getAccessCount();
}

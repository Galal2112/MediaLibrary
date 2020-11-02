package mediaDB;

import java.util.Collection;

public interface Content {
    String getAddress();
    void setAddress(String address);
    Collection<Tag> getTags();
    void setTags(Collection<Tag> tags);
    long getAccessCount();
    void setAccessCount(long count);
}

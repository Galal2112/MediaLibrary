package mediaDB;

import java.io.Serializable;
import java.math.BigDecimal;

public interface MediaContent extends Content, Serializable {
    long getBitrate();
    long getLength();
    BigDecimal getSize();
}

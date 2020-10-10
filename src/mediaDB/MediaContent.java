package mediaDB;

import java.math.BigDecimal;
import java.time.Duration;

public interface MediaContent extends Content {
    long getBitrate();
    Duration getLength();
    BigDecimal getSize();
}

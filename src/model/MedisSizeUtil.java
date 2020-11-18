package model;

import java.math.BigDecimal;
import java.time.Duration;

public interface MedisSizeUtil {

    /**
     * Estimate for media size based on bitrate and length
     *
     * @param bitrate Kbps
     * @param length Media duration
     * @return estimated size in MB
     */
     static BigDecimal getMediaSize(long bitrate, Duration length) {
         return new BigDecimal( bitrate * length.getSeconds() / 8).divide(new BigDecimal(1024));
    }
}

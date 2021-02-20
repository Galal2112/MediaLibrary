package model;

import java.math.BigDecimal;

public interface MediaSizeUtil {

    /**
     * Estimate for media size based on bitrate and length
     *
     * @param bitrate Kbps
     * @param length Media duration
     * @return estimated size in MB
     */
     static BigDecimal getMediaSize(long bitrate, long length) {
         return new BigDecimal( bitrate * length / 8).divide(new BigDecimal(1024));
    }
}

package com.reven.transfer.util;

/**
 * @author reven
 * @date 2020年10月19日
 */
public class TimeUtil {
    /**
     * change seconds to DayHourMinuteSecond format
     * 
     * @param startMs
     * @param endMs
     * @return
     */
    public static String ms2DHMS(long startMs, long endMs) {
        String retval = null;
        long secondCount = (endMs - startMs) / 1000;
        String ms = (endMs - startMs) % 1000 + "ms";

        long days = secondCount / (60 * 60 * 24);
        long hours = (secondCount % (60 * 60 * 24)) / (60 * 60);
        long minutes = (secondCount % (60 * 60)) / 60;
        long seconds = secondCount % 60;

        if (days > 0) {
            retval = days + "天" + hours + "小时" + minutes + "分钟" + seconds + "秒";
        } else if (hours > 0) {
            retval = hours + "小时" + minutes + "分钟" + seconds + "秒";
        } else if (minutes > 0) {
            retval = minutes + "分钟" + seconds + "秒";
        } else if (seconds > 0) {
            retval = seconds + "秒";
        } else {
            return ms;
        }

        return retval + ms;
    }
}

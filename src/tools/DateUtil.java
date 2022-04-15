package tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil
{
    private static final long FT_UT_OFFSET = 116444520000000000L;
    
    public static boolean isDST() {
        return TimeZone.getDefault().inDaylightTime(new Date());
    }
    
    public static long getFileTimestamp(final long timeStampinMillis) {
        return getFileTimestamp(timeStampinMillis, false);
    }
    
    public static String getCurrentDate() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }
    
    public static long getFileTimestamp(long timeStampinMillis, final boolean roundToMinutes) {
        if (isDST()) {
            timeStampinMillis -= 3600000L;
        }
        timeStampinMillis += 50400000L;
        long time;
        if (roundToMinutes) {
            time = timeStampinMillis / 1000L / 60L * 600000000L;
        }
        else {
            time = timeStampinMillis * 10000L;
        }
        return time + 116444520000000000L;
    }
    
    public static int getTime() {
        final String time = new SimpleDateFormat("yyyy-MM-dd-HH").format(new Date()).replace("-", "");
        return Integer.valueOf(time);
    }
    
    public static String getNowTime() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        return sdf.format(new Date());
    }
}

package util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Name: DateUtil
 * Description: Transfer the timestamp to Date String
 * Created on 2016/10/2 0002.
 */

public class DateUtil {
    private static SimpleDateFormat sf = null;

    public static String getDateNormal(long time) {
        if(time == 0)
            return "";

        Date d = new Date(time);
        sf = new SimpleDateFormat("MM/dd HH:mm");

        return sf.format(d);
    }
}

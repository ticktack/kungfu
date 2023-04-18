package org.kungfu.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateKit {
    /**
     * 获取当前时间(14位)
     *
     * @return
     */
    public static String getCurrentTime() {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(now);
    }

    public static String getCurrentDate() {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(now);
    }

    public static int getYear() {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        return Integer.parseInt(simpleDateFormat.format(now));
    }

    public static int getMonth() {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
        return Integer.parseInt(simpleDateFormat.format(now));
    }

    public static int getDay() {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return Integer.parseInt(simpleDateFormat.format(now));
    }
}

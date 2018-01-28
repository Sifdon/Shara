package org.seemsGood.shara.util;

import java.util.Calendar;

public class CalendarUtil {

    private static Calendar calendar;

    public static String getDate() {

        calendar = Calendar.getInstance();

        int d = calendar.get(Calendar.DAY_OF_MONTH);
        int m = calendar.get(Calendar.MONTH) + 1;
        int y = calendar.get(Calendar.YEAR);

        return (d > 9 ? "" : "0") + d + "." + (m > 9 ? "" : "0") + m + "." + y;
    }

    public static String getTime() {

        calendar = Calendar.getInstance();

        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        return (h > 9 ? "" : "0") + h + ":" + (min > 9 ? "" : "0") + min;
    }

    public static String getDateString(Calendar calendar){

        int d = calendar.get(Calendar.DAY_OF_MONTH);
        int m = calendar.get(Calendar.MONTH) + 1;
        int y = calendar.get(Calendar.YEAR);

        return (d > 9 ? "" : "0") + d + "." + (m > 9 ? "" : "0") + m + "." + y;
    }

}

package com.elogstation.client.elogstationdrive.util;

import com.elogstation.client.elogstationdrive.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainUtil {
    public static String convertDateToString(Date date){
        return dateFormat.format(date);
    }

    private static DateFormat dateFormat = new SimpleDateFormat(Constants.dateTimeFormat);
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(String date){
        try {
            Date newDate = dateFormat.parse(date);
            return getTimeAgo(newDate.getTime());
        }catch(Exception e){
            return "";
        }
    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        Date nowDate = new Date();
        long now = nowDate.getTime();

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour";
        } else if (diff < 24 * HOUR_MILLIS) {
            if(diff / HOUR_MILLIS == 1){
                return "1 hour";
            }
            return diff / HOUR_MILLIS + " hours";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            if(diff / DAY_MILLIS == 1){
                return "1 day";
            }
            return diff / DAY_MILLIS + " days";
        }
    }
}

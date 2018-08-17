package leotik.labs.gesturemessenger.Util;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Helper {
    public static String getTime(String timeinmilli) {

        long millis = Long.parseLong(timeinmilli);
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        millis = millis + mGMTOffset;

        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String Date = "";
        try {
            java.util.Date date = new SimpleDateFormat("D").parse(String.valueOf(days + 1));
            Date = formatter.format(date);
        } catch (Exception e) {
            Log.e("ritik", "getTime: " + e.getMessage());
        }
        StringBuilder sb = new StringBuilder(64);


        long current_days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
        if (current_days == days + 1) {
            sb.append("Yesterday");
            sb.append(" ");
        } else if (current_days != days) {
            sb.append(Date);
            sb.append(" ");
        }
        sb.append(hours);
        sb.append(":");
        sb.append(minutes);
        sb.append(":");
        sb.append(seconds);
        return (sb.toString());
    }

}

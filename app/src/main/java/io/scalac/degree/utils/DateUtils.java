package io.scalac.degree.utils;

import java.util.Calendar;
import java.util.Locale;

public abstract class DateUtils {

    public static long calculateDayStartMs(long date) {
        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 30);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        final long result = calendar.getTimeInMillis();
        calendar.clear();
        return result;
    }

    private DateUtils() {
        // Nothing.
    }
}

package io.scalac.degree.utils;

import android.util.Log;

import java.util.Date;

import io.scalac.degree33.BuildConfig;

public class Logger {

    public static final boolean isEnabled = BuildConfig.LOGGING;
    public static final String TAG = "Devoxx";

    public static void l(boolean message) {
        l(String.valueOf(message));
    }

    public static void l(int message) {
        l(String.valueOf(message));
    }

    public static void l(String message) {
        if (isEnabled) {
            Log.d(TAG, message);
        }
    }

    public static void logDate(String message, long time) {
        if (isEnabled) {
            Log.d(TAG, message + ", " + new Date(time).toString());
        }
    }

    public static void exc(Exception e) {
        if (isEnabled) {
            e.printStackTrace();
        }
    }
}

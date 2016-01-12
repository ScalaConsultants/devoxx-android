package io.scalac.degree.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.text.Spannable;
import android.widget.Toast;

public class InfoUtil {

    private static Toast toast;

//    private static MaterialDialog progressDialog;
//
//    public static void showProgressWithMessage(Context context, String title, String message) {
//        progressDialog = new MaterialDialog.Builder(context)
//                .title(title)
//                .content(message)
//                .progress(true, 0)
//                .build();
//
//        progressDialog.show();
//    }
//
//    public static void dismissProgressDialog() {
//        if (progressDialog != null) {
//            progressDialog.dismiss();
//        }
//    }

    public static void showToast(Context context, Spannable string) {
        showOnUiThreadIfNeeded(context, string);
    }

    public static void showToastLong(Context context, String string) {
        showOnUiThreadIfNeededLong(context, string);
    }


    public static void showToast(Context context, String string) {
        showOnUiThreadIfNeeded(context, string);
    }

    public static void showToast(Context context, @StringRes int string) {
        showOnUiThreadIfNeeded(context, string);
    }

    private static void showOnUiThreadIfNeeded(Context context, @StringRes int string) {
        showHelper(context, context.getResources().getString(string));
    }

    private static void showOnUiThreadIfNeededLong(Context context, String string) {
        showHelper(context, string, Toast.LENGTH_LONG);
    }

    private static void showOnUiThreadIfNeeded(Context context, String string) {
        showHelper(context, string);
    }

    private static void showOnUiThreadIfNeeded(Context context, Spannable string) {
        showHelper(context, string.toString());
    }

    private static void showHelper(final Context context, final String s, final int sec) {
        final Runnable showRunnable = new Runnable() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.setText(s);
                    if (!toast.getView().isShown()) {
                        toast.show();
                    }
                } else {
                    toast = Toast.makeText(context, s, sec);
                    toast.show();
                }
            }
        };

        if (isMainThread()) {
            showRunnable.run();
        } else {
            new Handler(Looper.getMainLooper()).post(showRunnable);
        }
    }

    private static void showHelper(final Context context, final String s) {
        showHelper(context, s, Toast.LENGTH_SHORT);
    }

    private static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }
}

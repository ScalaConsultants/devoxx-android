package io.scalac.degree.utils;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.text.Spannable;
import android.widget.Toast;

@EBean
public class InfoUtil {

    private static Toast toast;

    public void showToast(Context context, Spannable string) {
        showHelper(context, string.toString());
    }

    public void showToastLong(Context context, String string) {
        showOnUiThreadIfNeededLong(context, string);
    }


    public void showToast(Context context, String string) {
        showOnUiThreadIfNeeded(context, string);
    }

    public void showToast(Context context, @StringRes int string) {
        showOnUiThreadIfNeeded(context, string);
    }

    private void showOnUiThreadIfNeeded(Context context, @StringRes int string) {
        showHelper(context, context.getResources().getString(string));
    }

    private void showOnUiThreadIfNeededLong(Context context, String string) {
        showHelper(context, string, Toast.LENGTH_LONG);
    }

    private void showOnUiThreadIfNeeded(Context context, String string) {
        showHelper(context, string);
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    protected void showHelper(final Context context, final String s, final int sec) {
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

    private void showHelper(final Context context, final String s) {
        showHelper(context, s, Toast.LENGTH_SHORT);
    }
}

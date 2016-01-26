package com.devoxx.utils;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.Spannable;
import android.widget.Toast;

@EBean(scope = EBean.Scope.Singleton)
public class InfoUtil {

    @RootContext
    Context context;

    private Toast toast;

    public void showToast(Spannable string) {
        showHelper(context, string.toString());
    }

    public void showToastLong(String string) {
        showOnUiThreadIfNeededLong(string);
    }

    public void showToast(String string) {
        showOnUiThreadIfNeeded(string);
    }

    public void showToast(@StringRes int string) {
        showOnUiThreadIfNeeded(string);
    }

    private void showOnUiThreadIfNeeded(@StringRes int string) {
        showHelper(context, context.getResources().getString(string));
    }

    private void showOnUiThreadIfNeededLong(String string) {
        showHelper(context, string, Toast.LENGTH_LONG);
    }

    private void showOnUiThreadIfNeeded(String string) {
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

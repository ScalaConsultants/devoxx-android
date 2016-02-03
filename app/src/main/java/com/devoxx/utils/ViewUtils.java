package com.devoxx.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

@EBean
public class ViewUtils {
    @RootContext
    Context context;

    public boolean setTextOrHide(final TextView view, final CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            view.setVisibility(View.GONE);
            view.setText(null);
            return false;
        } else {
            view.setVisibility(View.VISIBLE);
            view.setText(text);
            return true;
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        final int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}

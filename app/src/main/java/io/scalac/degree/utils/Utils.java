package io.scalac.degree.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.EBean;

import io.scalac.degree33.R;

@EBean
public class Utils {

    public static final String FLURRY_API_KEY = "FQNW22QNG8F9DG7JFFNS";

    @NonNull
    public static View getFooterView(
            @NonNull Activity activity, @NonNull ViewGroup parent) {
        return activity.getLayoutInflater().inflate(R.layout.footer_scalac_button, parent, false);
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Logger.exc(e);
            return "versionName";
        }
    }

    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            Logger.exc(e);
            return 0;
        }
    }
}

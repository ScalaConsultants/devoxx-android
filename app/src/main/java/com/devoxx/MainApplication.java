package com.devoxx;

import com.crashlytics.android.Crashlytics;

import org.androidannotations.annotations.EApplication;

import android.app.Application;
import android.os.Build;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

import io.fabric.sdk.android.Fabric;
import net.danlew.android.joda.JodaTimeAndroid;

@EApplication
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            try {
                ViewConfiguration config = ViewConfiguration.get(this);
                Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
                if (menuKeyField != null) {
                    menuKeyField.setAccessible(true);
                    menuKeyField.setBoolean(config, false);
                }
            } catch (Exception ex) {
                // Ignore
            }

        JodaTimeAndroid.init(this);
    }
}

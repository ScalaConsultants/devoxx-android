package com.devoxx.android.activity;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;

import com.devoxx.utils.DeviceUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

@EActivity
public abstract class BaseActivity extends AppCompatActivity {

    @Bean
    DeviceUtil deviceUtil;

    @AfterViews
    void afterViews() {
        if (deviceUtil.isTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
    }
}

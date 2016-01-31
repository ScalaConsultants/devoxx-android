package com.devoxx.utils;

import android.content.Context;
import android.content.res.Configuration;

import com.devoxx.R;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.res.BooleanRes;

@EBean
public class DeviceUtil {

    @RootContext
    Context context;

    @BooleanRes(R.bool.is_tablet)
    boolean isTablet;

    public boolean isLandscapeTablet() {
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE && isTablet;
    }

    public boolean isTablet(){
        return isTablet;
    }
}

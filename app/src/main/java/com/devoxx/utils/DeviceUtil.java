package com.devoxx.utils;

import android.content.Context;
import android.content.res.Configuration;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

@EBean
public class DeviceUtil {

    @RootContext
    Context context;

    public boolean isLandscapeTablet() {
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }
}

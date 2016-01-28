package com.devoxx.utils;

import android.app.ActivityManager;
import android.content.Context;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import java.util.List;

@EBean
public class ActivityUtils {

    @RootContext
    Context context;

    @SystemService
    ActivityManager activityManager;

    public boolean isAppForeground(Context context) {
        List<ActivityManager.RunningAppProcessInfo> runningProcesses
                = activityManager.getRunningAppProcesses();

        final String outPackageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo runningProcess : runningProcesses) {
            for (String packageName : runningProcess.pkgList) {
                if (outPackageName.equals(packageName)) {
                    return runningProcess.importance ==
                            ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                }
            }
        }

        return false;
    }
}

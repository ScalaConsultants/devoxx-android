package com.devoxx.android.receiver;

import com.devoxx.data.manager.NotificationsManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;

@EReceiver
public class AppUpdatedReceiver extends BroadcastReceiver {

    @Bean
    NotificationsManager notificationsManager;

    public AppUpdatedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getDataString().contains(context.getPackageName())) {
            // TODO handle it!
            // notificationsManager.resetAlarms();
        }
    }
}

package io.scalac.degree.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;

import io.scalac.degree.data.manager.NotificationsManager;

@EReceiver
public class AppUpdatedReceiver extends BroadcastReceiver {

    @Bean
    NotificationsManager notificationsManager;

    public AppUpdatedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getDataString().contains(context.getPackageName())) {
            notificationsManager.resetAlarms();
        }
    }
}

package io.scalac.degree.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;

import io.scalac.degree.data.manager.NotificationsManager;

@EReceiver
public class BootReceiver extends BroadcastReceiver {

    @Bean
    NotificationsManager notificationsManager;

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationsManager.resetAlarms();
    }
}

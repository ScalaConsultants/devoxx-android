package io.scalac.degree.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;

import io.scalac.degree.data.manager.NotificationsManager;

@EReceiver
public class TimezoneReceiver extends BroadcastReceiver {

    @Bean
    NotificationsManager naNotificationsManager;

    public TimezoneReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        naNotificationsManager.resetAlarms();
    }
}

package com.devoxx.android.receiver;

import com.devoxx.data.manager.NotificationsManager;
import com.devoxx.utils.Logger;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.SystemService;

@EReceiver
public class AlarmReceiver extends BroadcastReceiver {

    @Bean
    NotificationsManager notificationsManager;
    @SystemService
    PowerManager powerManager;

    public AlarmReceiver() {
    }

    @SuppressLint("Wakelock")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras().containsKey(NotificationsManager.EXTRA_NOTIFICATION_TYPE)) {
            Logger.l("Alarm.forPost");
            handlePostTalkEvent(intent);
        } else {
            Logger.l("Alarm.forNormal");
            handleIncomingTalkEvent(intent);
        }
    }

    private void handlePostTalkEvent(Intent intent) {
        final String slotId = intent.getStringExtra(NotificationsManager.EXTRA_TALK_ID);

        // TODO Texts probably will change.
        notificationsManager.showPostNotification(slotId, "Give a vote!", "Add vote for the talk.");
    }

    private void handleIncomingTalkEvent(Intent intent) {
        final PowerManager.WakeLock wl = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "AlarmService");
        wl.acquire();
        final String slotId = intent.getStringExtra(NotificationsManager.EXTRA_TALK_ID);
        notificationsManager.showNotification(slotId);
        wl.release();
    }
}

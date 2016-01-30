package com.devoxx.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.devoxx.data.manager.NotificationsManager;
import com.devoxx.utils.Logger;

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
        // Nothing.
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(NotificationsManager.EXTRA_TALK_ID)) {
            final String slotId = intent.getStringExtra(NotificationsManager.EXTRA_TALK_ID);
            if (notificationsManager.isNotificationAvailable(slotId)) {
                if (intent.getExtras().containsKey(NotificationsManager.EXTRA_NOTIFICATION_TYPE)) {
                    Logger.l("Alarm.forPost");
                    handlePostTalkEvent(slotId);
                } else {
                    Logger.l("Alarm.forNormal");
                    handleIncomingTalkEvent(slotId);
                }
            } else {
                Logger.l("Alarm.doNothing.notification.expired");
            }
        } else {
            Logger.l("Alarm.doNothing.noExtraTalkId");
        }
    }

    private void handlePostTalkEvent(final String slotId) {
        final PowerManager.WakeLock wl = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "AlarmService");
        wl.acquire();
        // TODO Texts probably will change.
        notificationsManager.showNotificationForVote(slotId, "Give a vote!", "Add vote for the talk.");
        wl.release();
    }

    private void handleIncomingTalkEvent(final String slotId) {
        final PowerManager.WakeLock wl = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "AlarmService");
        wl.acquire();
        notificationsManager.showNotificationForTalk(slotId);
        wl.release();
    }
}

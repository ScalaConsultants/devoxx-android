package com.devoxx.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.text.TextUtils;

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
        Logger.l("Alarm.onReceive");

        final String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            Logger.l("Alarm.onReceive.withAction: " + action);

            final String slotId = intent.getStringExtra(NotificationsManager.EXTRA_TALK_ID);

            if (!notificationsManager.isNotificationAvailable(slotId)) {
                Logger.l("Alarm.doNothing.notification.unavailable");
                return;
            }

            final PowerManager.WakeLock wl = powerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK, "AlarmService");
            wl.acquire();
            if (NotificationsManager.NOTIFICATION_TALK_TYPE.equalsIgnoreCase(action)) {
                Logger.l("Alarm.forNormal");
                handleTalkEvent(slotId);
            } else if (NotificationsManager.NOTIFICATION_POST_TYPE.equalsIgnoreCase(action)) {
                Logger.l("Alarm.forPost");
                handlePostTalkEvent(slotId);
            } else {
                Logger.l("Alarm.forNothing");
            }
            wl.release();

        }
    }

    private void handlePostTalkEvent(final String slotId) {
        notificationsManager.showNotificationForVote(slotId, "Give a vote!", "Add vote for the talk.");
    }

    private void handleTalkEvent(final String slotId) {
        notificationsManager.showNotificationForTalk(slotId);
    }
}

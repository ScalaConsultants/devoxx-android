package io.scalac.degree.android.receiver;

import com.flurry.android.FlurryAgent;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.SystemService;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import io.scalac.degree.data.manager.NotificationsManager;

@EReceiver
public class AlarmReceiver extends BroadcastReceiver {

	@Bean NotificationsManager notificationsManager;
	@SystemService PowerManager powerManager;

	public AlarmReceiver() {
	}

	@SuppressLint("Wakelock")
	@Override public void onReceive(Context context, Intent intent) {
		final PowerManager.WakeLock wl = powerManager.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "AlarmService");
		wl.acquire();
		final String slotId = intent.getStringExtra(NotificationsManager.EXTRA_TALK_ID);
		notificationsManager.showNotification(slotId);
		wl.release();
		FlurryAgent.logEvent("Alarm_received");
	}
}

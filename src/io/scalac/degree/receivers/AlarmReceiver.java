package io.scalac.degree.receivers;

import io.scalac.degree.utils.Utils;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.flurry.android.FlurryAgent;

public class AlarmReceiver extends BroadcastReceiver {
	
	public AlarmReceiver() {}
	
	@SuppressLint("Wakelock")
	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlarmService");
		wl.acquire();
		// Toast.makeText(context, "EXTRA_TALK_ID: " + intent.getIntExtra(Utils.EXTRA_TALK_ID, -1),
		// Toast.LENGTH_LONG).show();
		int talkID = intent.getIntExtra(Utils.EXTRA_TALK_ID, -1);
		if (talkID != -1)
			Utils.showNotification(context.getApplicationContext(), talkID);
		wl.release();
		FlurryAgent.logEvent("Alarm_received");
	}
}

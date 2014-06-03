package io.scalac.degree.receivers;

import io.scalac.degree.Utils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppUpdatedReceiver extends BroadcastReceiver {
	public AppUpdatedReceiver() {}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getDataString().contains(context.getPackageName())) {
			Utils.resetAlarms(context.getApplicationContext());
		}
	}
}

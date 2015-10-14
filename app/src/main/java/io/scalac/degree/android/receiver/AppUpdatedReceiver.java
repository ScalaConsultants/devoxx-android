package io.scalac.degree.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.scalac.degree.utils.Utils;

public class AppUpdatedReceiver extends BroadcastReceiver {
	public AppUpdatedReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getDataString().contains(context.getPackageName())) {
			Utils.resetAlarms(context.getApplicationContext());
		}
	}
}

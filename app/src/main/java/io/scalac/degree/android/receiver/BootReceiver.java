package io.scalac.degree.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.scalac.degree.utils.Utils;

public class BootReceiver extends BroadcastReceiver {
	public BootReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Utils.resetAlarms(context.getApplicationContext());
	}
}

package io.scalac.degree.receivers;

import io.scalac.degree.utils.Utils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimezoneReceiver extends BroadcastReceiver {
	public TimezoneReceiver() {}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Utils.resetAlarms(context.getApplicationContext());
	}
}

package io.scalac.degree;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
	
	public static final String	NOTIFICATIONS_NAME	= "notifications";
	
	public static String getRawResource(Context context, int id) {
		try {
			InputStream is = context.getResources().openRawResource(id);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			// Convert the buffer into a string.
			return new String(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void setNotify(Context context, int talkID, long timeMS) {
		SharedPreferences notifications = context.getSharedPreferences(Utils.NOTIFICATIONS_NAME, 0);
		SharedPreferences.Editor editor = notifications.edit();
	}
}

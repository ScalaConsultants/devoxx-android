package io.scalac.degree.utils;

import io.scalac.degree.MainActivity;
import io.scalac.degree.R;
import io.scalac.degree.items.RoomItem;
import io.scalac.degree.items.TalkItem;
import io.scalac.degree.items.TimeslotItem;
import io.scalac.degree.receivers.AlarmReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class Utils {
	
	public static final String	ALARMS_NAME		= "alarms";
	public static final String	EXTRA_TALK_ID	= "io.scalac.degree.android.intent.extra.TALK_ID";
	
	public static void showNotification(Context context, int talkID) {
		ArrayList<TimeslotItem> timeslotItemsList = getTimeslotItemsList(context);
		ArrayList<TalkItem> talkItemsList = getTalkItemsList(context, timeslotItemsList);
		TalkItem talkItem = TalkItem.getByID(talkID, talkItemsList);
		
		if (talkItem.getStartTime().getTime() < System.currentTimeMillis() - 600000) {
			unsetNotify(context, talkID);
		} else {
			ArrayList<RoomItem> roomItemsList = getRoomItemsList(context);
			Intent notificationIntent = new Intent(context, MainActivity.class);
			notificationIntent.putExtra(EXTRA_TALK_ID, talkID);
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent contentIntent = PendingIntent.getActivity(context,
					talkID,
					notificationIntent,
					PendingIntent.FLAG_CANCEL_CURRENT);
			
			NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
			try {
				RoomItem roomItem = RoomItem.getByID(talkItem.getRoomID(), roomItemsList);
				notificationBuilder.setContentTitle(roomItem.getName());
				notificationBuilder.setContentText(talkItem.getTopic());
			} catch (ItemNotFoundException e) {
				notificationBuilder.setContentTitle(talkItem.getTopic());
				e.printStackTrace();
			}
			notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(talkItem.getTopic()));
			notificationBuilder.setContentIntent(contentIntent);
			notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
			// notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), iconLarge));
			notificationBuilder.setWhen(talkItem.getStartTime().getTime());
			notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
			notificationBuilder.setTicker(talkItem.getTopic());
			// notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
			
			Notification notification = notificationBuilder.build();
			
			// notification.flags |= Notification.FLAG_ONGOING_EVENT;
			// notification.flags |= Notification.FLAG_NO_CLEAR;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.defaults |= Notification.DEFAULT_LIGHTS;
			
			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(talkID, notification);
			// unsetNotify(context, talkID);
		}
	}
	
	public static ArrayList<TimeslotItem> getTimeslotItemsList(Context context) {
		ArrayList<TimeslotItem> timeslotItemsList = new ArrayList<TimeslotItem>();
		try {
			JSONArray jsonArray = new JSONArray(Utils.getRawResource(context, R.raw.timeslots));
			TimeslotItem.fillList(timeslotItemsList, jsonArray);
		} catch (NotFoundException e) {
			// e.printStackTrace();
		} catch (JSONException e) {
			// e.printStackTrace();
		}
		return timeslotItemsList;
	}
	
	public static ArrayList<TalkItem> getTalkItemsList(Context context, ArrayList<TimeslotItem> timeslotItemsList) {
		ArrayList<TalkItem> talkItemsList = new ArrayList<TalkItem>();
		try {
			JSONArray jsonArray = new JSONArray(Utils.getRawResource(context, R.raw.talks));
			TalkItem.fillList(talkItemsList, jsonArray, timeslotItemsList);
		} catch (NotFoundException e) {
			// e.printStackTrace();
		} catch (JSONException e) {
			// e.printStackTrace();
		}
		return talkItemsList;
	}
	
	public static ArrayList<RoomItem> getRoomItemsList(Context context) {
		ArrayList<RoomItem> roomItemsList = new ArrayList<RoomItem>();
		try {
			JSONArray jsonArray = new JSONArray(Utils.getRawResource(context, R.raw.rooms));
			RoomItem.fillList(roomItemsList, jsonArray);
		} catch (NotFoundException e) {
			// e.printStackTrace();
		} catch (JSONException e) {
			// e.printStackTrace();
		}
		return roomItemsList;
	}
	
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
	
	public static Map<String, ?> getAlarms(Context context) {
		Map<String, ?> keys = context.getSharedPreferences(Utils.ALARMS_NAME, 0).getAll();
		return keys;
	}
	
	@SuppressLint("Wakelock")
	public static void resetAlarms(Context context) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlarmService");
		wl.acquire();
		Map<String, ?> notificationsMap = getAlarms(context);
		if (!notificationsMap.isEmpty()) {
			ArrayList<TimeslotItem> timeslotItemsList = getTimeslotItemsList(context);
			ArrayList<TalkItem> talkItemsList = getTalkItemsList(context, timeslotItemsList);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, AlarmReceiver.class);
			for (Map.Entry<String, ?> entry : notificationsMap.entrySet()) {
				int talkID = Integer.valueOf(entry.getKey());
				intent.putExtra(EXTRA_TALK_ID, talkID);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, talkID, intent, 0);
				alarmManager.cancel(pendingIntent);
				
				TalkItem talkItem = TalkItem.getByID(talkID, talkItemsList);
				if (talkItem != null) {
					setNotify(context, talkID, talkItem.getStartTime().getTime(), false);
				} else
					unsetNotify(context, talkID);
			}
		}
		wl.release();
	}
	
	private static long getAlarmTime(Context context, long talkStartMS) {
		// return talkStartMS - 600000; //10 mins before start
		return System.currentTimeMillis() + 3000; // 3sec after set for testing
	}
	
	public static boolean isNotifySet(Context context, int talkID) {
		SharedPreferences notifications = context.getSharedPreferences(Utils.ALARMS_NAME, 0);
		return notifications.contains(String.valueOf(talkID));
	}
	
	public static void setNotify(Context context, int talkID, long talkStartMS, boolean showToast) {
		SharedPreferences notifications = context.getSharedPreferences(Utils.ALARMS_NAME, 0);
		
		SharedPreferences.Editor editor = notifications.edit();
		editor.putLong(String.valueOf(talkID), talkStartMS);
		editor.commit();
		
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra(EXTRA_TALK_ID, talkID);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, talkID, intent, 0);
		long alarmTime = getAlarmTime(context, talkStartMS);
		setAlarm(context, alarmTime, pendingIntent);
		
		if (showToast) {
			DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
			DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
			String time = timeFormat.format(alarmTime);
			String date = dateFormat.format(alarmTime);
			if (!dateFormat.format(System.currentTimeMillis()).equals(date)) {
				time = "\n" + date + " " + time;
			}
			Toast.makeText(context, context.getString(R.string.toast_notification) + " " + time, Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	public static void unsetNotify(Context context, int talkID) {
		SharedPreferences notifications = context.getSharedPreferences(Utils.ALARMS_NAME, 0);
		
		SharedPreferences.Editor editor = notifications.edit();
		editor.remove(String.valueOf(talkID));
		editor.commit();
		
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra(EXTRA_TALK_ID, talkID);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, talkID, intent, 0);
		alarmManager.cancel(pendingIntent);
	}
	
	private static void setAlarm(Context context, long triggerAtMillis, PendingIntent operation) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
			alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, operation);
		else {
			setExactAlarm(context, triggerAtMillis, operation);
		}
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private static void setExactAlarm(Context context, long triggerAtMillis, PendingIntent operation) {
		try {
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, operation);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

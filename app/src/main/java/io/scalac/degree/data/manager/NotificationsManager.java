package io.scalac.degree.data.manager;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.scalac.degree.android.activity.MainActivity_;
import io.scalac.degree.android.receiver.AlarmReceiver_;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.RealmProvider;
import io.scalac.degree.data.model.NotificationModel;
import io.scalac.degree.utils.Logger;
import io.scalac.degree33.BuildConfig;
import io.scalac.degree33.R;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 03/11/2015
 */
@EBean
public class NotificationsManager {

    public static final String EXTRA_TALK_ID = "io.scalac.degree.android.intent.extra.TALK_ID";

    @RootContext
    Context context;
    @Bean
    RealmProvider realmProvider;

    @SystemService
    AlarmManager alarmManager;
    @SystemService
    NotificationManager notificationManager;
    @SystemService
    PowerManager powerManager;

    public boolean scheduleNotification(ScheduleNotificationModel scheduleNotificationModel) {
        final long talkStartTimeMs = scheduleNotificationModel.eventTime;
        final boolean showToast = scheduleNotificationModel.showToast;
        final long alarmTime = calculateAlarmTime(talkStartTimeMs);
        scheduleNotificationModel.setAlarmTime(alarmTime);

        if (alarmTime < System.currentTimeMillis()) {
            if (showToast) {
                Toast.makeText(context,
                        context.getString(R.string.toast_notification_not_set),
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        final String slotId = scheduleNotificationModel.slotId;

        storeNotification(scheduleNotificationModel);
        scheduleAlarm(context, alarmTime, slotId);

        if (showToast) {
            final DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
            final DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
            final String date = dateFormat.format(alarmTime);
            String time = timeFormat.format(alarmTime);

            if (!dateFormat.format(System.currentTimeMillis()).equals(date)) {
                time = "\n" + date + " " + time;
            }

            Toast.makeText(context, context.getString(R.string.toast_notification_set_at) +
                    " " + time, Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    public void unscheduleNotification(String slotId) {
        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        realm.where(NotificationModel.class).equalTo("slotId", slotId).findAll().clear();
        realm.commitTransaction();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver_.class);
        intent.putExtra(EXTRA_TALK_ID, slotId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                slotId.hashCode(), intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public boolean isNotificationScheduled(String slotId) {
        final Realm realm = realmProvider.getRealm();
        return realm.where(NotificationModel.class)
                .equalTo("slotId", slotId).count() > 0;
    }

    public void showNotification(String slotId) {
        final Realm realm = realmProvider.getRealm();
        final NotificationModel notificationModel = realm
                .where(NotificationModel.class)
                .equalTo("slotId", slotId)
                .findFirst();

        if (isNotificationBeforeEvent(notificationModel)) {
            Intent notificationIntent = new Intent(context, MainActivity_.class);
            notificationIntent.putExtra(EXTRA_TALK_ID, slotId);
            PendingIntent contentIntent = PendingIntent.getActivity(context,
                    slotId.hashCode(),
                    notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
            notificationBuilder.setContentTitle(notificationModel.getRoomName());
            notificationBuilder.setContentText(notificationModel.getTalkName());
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(notificationModel.getTalkName()));
            notificationBuilder.setContentIntent(contentIntent);
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
            notificationBuilder.setWhen(notificationModel.getEventTime());
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
            notificationBuilder.setTicker(notificationModel.getTalkName());

            Notification notification = notificationBuilder.build();
            // notification.flags |= Notification.FLAG_ONGOING_EVENT;
            // notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.defaults |= Notification.DEFAULT_LIGHTS;

            notificationManager.notify(slotId.hashCode(), notification);
            unscheduleNotification(slotId);
        } else {
            unscheduleNotification(slotId);
        }
    }

    private boolean isNotificationBeforeEvent(NotificationModel notificationModel) {
        return BuildConfig.DEBUG || notificationModel.getEventTime()
                > System.currentTimeMillis() - 600000;
    }

    public List<NotificationModel> getAlarms() {
        final Realm realm = realmProvider.getRealm();
        return realm.where(NotificationModel.class).findAll();
    }

    @SuppressLint("Wakelock")
    public void resetAlarms() {
        final PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "AlarmService");
        wakeLock.acquire();
        final List<NotificationModel> notificationsList = getAlarms();
        if (!notificationsList.isEmpty()) {
            final Intent intent = new Intent(context, AlarmReceiver_.class);
            for (NotificationModel model : notificationsList) {
                final String slotId = model.getSlotId();
                intent.putExtra(EXTRA_TALK_ID, slotId);
                final PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                        slotId.hashCode(), intent, 0);
                alarmManager.cancel(pendingIntent);

                final ScheduleNotificationModel schModel = ScheduleNotificationModel
                        .create(model, false);
                scheduleNotification(schModel);
            }
        }
        wakeLock.release();
    }

    public long calculateAlarmTime(long talkStartMS) {
        return BuildConfig.DEBUG
                ? System.currentTimeMillis() + 3000 // 3sec after set for testing
                : talkStartMS - 600000; // 10 mins before start
    }

    private void storeNotification(ScheduleNotificationModel notifyModel) {
        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        final NotificationModel model = new NotificationModel();
        model.setAlarmTime(notifyModel.alarmTime);
        model.setEventTime(notifyModel.eventTime);
        model.setSlotId(notifyModel.slotId);
        model.setRoomName(notifyModel.roomName);
        model.setTalkName(notifyModel.talkName);
        realm.copyToRealmOrUpdate(model);
        realm.commitTransaction();
    }

    private void scheduleAlarm(
            Context context, long triggerAtMillis, String slotId) {
        Logger.logDate("Schedule alarm, from: " +
                new Date(System.currentTimeMillis()), triggerAtMillis);

        final Intent intent = new Intent(context, AlarmReceiver_.class);
        intent.putExtra(EXTRA_TALK_ID, slotId);
        final PendingIntent pendingIntent = PendingIntent
                .getBroadcast(context, slotId.hashCode(), intent, 0);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }

    public static class ScheduleNotificationModel {
        private String slotId;
        private String roomName;
        private String talkName;
        private long eventTime;
        private long alarmTime;
        private boolean showToast;

        public static ScheduleNotificationModel create(
                NotificationModel notificationModel, boolean showToast) {
            final ScheduleNotificationModel result = new ScheduleNotificationModel();
            result.roomName = notificationModel.getRoomName();
            result.talkName = notificationModel.getTalkName();
            result.slotId = notificationModel.getSlotId();
            result.eventTime = notificationModel.getEventTime();
            result.showToast = showToast;
            return result;
        }

        public static ScheduleNotificationModel create(
                SlotApiModel slotApiModel, boolean showToast) {
            final ScheduleNotificationModel result = new ScheduleNotificationModel();
            result.roomName = slotApiModel.roomName;
            result.talkName = slotApiModel.talk.title;
            result.slotId = slotApiModel.slotId;
            result.eventTime = slotApiModel.fromTimeMillis;
            result.showToast = showToast;
            return result;
        }

        public void setAlarmTime(long alarmTime) {
            this.alarmTime = alarmTime;
        }
    }
}

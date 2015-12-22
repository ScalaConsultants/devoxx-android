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
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.scalac.degree.android.activity.MainActivity_;
import io.scalac.degree.android.receiver.AlarmReceiver_;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.RealmProvider;
import io.scalac.degree.data.model.RealmNotification;
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
    private static final long POST_TALK_NOTIFICATION_DELAY_MINUTES = 1;
    public static final String EXTRA_NOTIFICATION_TYPE = "io.scalac.degree.android.intent.extra.EXTRA_NOTIFICATION_TYPE";
    private static final long DEBUG_BEFORE_TALK_NOTIFICATION_SPAN_MS = TimeUnit.SECONDS.toMillis(10);
    private static final long PROD_BEFORE_TALK_NOTIFICATION_SPAN_MS = TimeUnit.HOURS.toMillis(1);
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

        final long beforeTalkNotification = BuildConfig.DEBUG ?
                DEBUG_BEFORE_TALK_NOTIFICATION_SPAN_MS : PROD_BEFORE_TALK_NOTIFICATION_SPAN_MS;

        final long alarmTime = calculateAlarmTime(talkStartTimeMs, beforeTalkNotification);
        scheduleNotificationModel.setAlarmTime(alarmTime);

        if (alarmTime < System.currentTimeMillis()) {
            if (showToast) {
                Toast.makeText(context,
                        context.getString(R.string.toast_notification_not_set),
                        Toast.LENGTH_SHORT).show();
            }

            Logger.l("Can't set alarm for talk notification: " + new Date(alarmTime));

            return false;
        }

        final String slotId = scheduleNotificationModel.slotId;

        storeNotification(scheduleNotificationModel);
        scheduleAlarm(context, alarmTime, slotId);

        final long postTime = alarmTime + beforeTalkNotification + TimeUnit.MINUTES.
                toMillis(POST_TALK_NOTIFICATION_DELAY_MINUTES);
        schedulePostAlarm(context, postTime, slotId);

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

    private void schedulePostAlarm(Context context, long postTime, String slotId) {
        Logger.logDate("Schedule POST alarm, on: " +
                new Date(System.currentTimeMillis()), postTime);

        final Intent intent = new Intent(context, AlarmReceiver_.class);
        intent.putExtra(EXTRA_TALK_ID, slotId);
        intent.putExtra(EXTRA_NOTIFICATION_TYPE, "post_notification");
        final PendingIntent pendingIntent = PendingIntent
                .getBroadcast(context, (int) (slotId.hashCode() + postTime), intent, 0);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, postTime, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, postTime, pendingIntent);
        }
    }

    public void unscheduleNotification(String slotId, boolean removeRealmModel) {
        final Realm realm = realmProvider.getRealm();
        if (removeRealmModel) {
            realm.beginTransaction();
            realm.where(RealmNotification.class).equalTo(RealmNotification.Contract.SLOT_ID,
                    slotId).findAll().clear();
            realm.commitTransaction();
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver_.class);
        intent.putExtra(EXTRA_TALK_ID, slotId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                slotId.hashCode(), intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public boolean isNotificationScheduled(String slotId) {
        final Realm realm = realmProvider.getRealm();
        return realm.where(RealmNotification.class)
                .equalTo(RealmNotification.Contract.SLOT_ID, slotId).count() > 0;
    }

    public void showPostNotification(String slotId, String title, String desc) {
        final Realm realm = realmProvider.getRealm();
        final RealmNotification realmNotification = realm
                .where(RealmNotification.class)
                .equalTo(RealmNotification.Contract.SLOT_ID, slotId)
                .findFirst();

        if (isNotificationBeforeEvent(realmNotification)) {
            Intent notificationIntent = new Intent(context, MainActivity_.class);
            notificationIntent.putExtra(EXTRA_TALK_ID, slotId);
            PendingIntent contentIntent = PendingIntent.getActivity(context,
                    slotId.hashCode(),
                    notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
            notificationBuilder.setContentTitle(title);
            notificationBuilder.setContentText(desc);
            notificationBuilder.setContentIntent(contentIntent);
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
            notificationBuilder.setWhen(realmNotification.getEventTime());
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
            notificationBuilder.setTicker(desc);

            Notification notification = notificationBuilder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.defaults |= Notification.DEFAULT_LIGHTS;

            notificationManager.notify(slotId.hashCode(), notification);
            unscheduleNotification(slotId, true);
        } else {
            unscheduleNotification(slotId, true);
        }
    }

    public void showNotification(String slotId) {
        final Realm realm = realmProvider.getRealm();
        final RealmNotification realmNotification = realm
                .where(RealmNotification.class)
                .equalTo(RealmNotification.Contract.SLOT_ID, slotId)
                .findFirst();

        if (isNotificationBeforeEvent(realmNotification)) {
            Intent notificationIntent = new Intent(context, MainActivity_.class);
            notificationIntent.putExtra(EXTRA_TALK_ID, slotId);
            PendingIntent contentIntent = PendingIntent.getActivity(context,
                    slotId.hashCode(),
                    notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
            notificationBuilder.setContentTitle(realmNotification.getRoomName());
            notificationBuilder.setContentText(realmNotification.getTalkName());
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(realmNotification.getTalkName()));
            notificationBuilder.setContentIntent(contentIntent);
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
            notificationBuilder.setWhen(realmNotification.getEventTime());
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
            notificationBuilder.setTicker(realmNotification.getTalkName());

            Notification notification = notificationBuilder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.defaults |= Notification.DEFAULT_LIGHTS;

            notificationManager.notify(slotId.hashCode(), notification);
            unscheduleNotification(slotId, false);
        } else {
            unscheduleNotification(slotId, false);
        }
    }

    private boolean isNotificationBeforeEvent(RealmNotification realmNotification) {
        return BuildConfig.DEBUG || realmNotification.getEventTime()
                > System.currentTimeMillis() - 600000;
    }

    public List<RealmNotification> getAlarms() {
        final Realm realm = realmProvider.getRealm();
        return realm.where(RealmNotification.class).findAll();
    }

    @SuppressLint("Wakelock")
    public void resetAlarms() {
        final PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "AlarmService");
        wakeLock.acquire();
        final List<RealmNotification> notificationsList = getAlarms();
        if (!notificationsList.isEmpty()) {
            final Intent intent = new Intent(context, AlarmReceiver_.class);
            for (RealmNotification model : notificationsList) {
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

    public long calculateAlarmTime(long talkStartMS, long beforeTalkTimeSpan) {
        return BuildConfig.DEBUG
                ? System.currentTimeMillis() + beforeTalkTimeSpan
                : talkStartMS - beforeTalkTimeSpan;
    }

    private void storeNotification(ScheduleNotificationModel notifyModel) {
        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        final RealmNotification model = new RealmNotification();
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
                RealmNotification realmNotification, boolean showToast) {
            final ScheduleNotificationModel result = new ScheduleNotificationModel();
            result.roomName = realmNotification.getRoomName();
            result.talkName = realmNotification.getTalkName();
            result.slotId = realmNotification.getSlotId();
            result.eventTime = realmNotification.getEventTime();
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

package com.devoxx.data.manager;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import java.text.DateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

import com.devoxx.android.activity.MainActivity_;
import com.devoxx.android.receiver.AlarmReceiver_;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.data.RealmProvider;
import com.devoxx.data.model.RealmNotification;

import com.devoxx.BuildConfig;
import com.devoxx.R;

@EBean
public class NotificationsManager {

    public static final String EXTRA_TALK_ID = "com.devoxx.android.intent.extra.TALK_ID";
    public static final String EXTRA_NOTIFICATION_TYPE = "com.devoxx.android.intent.extra.EXTRA_NOTIFICATION_TYPE";
    public static final String EXTRA_NOTIFICATION_TYPE_VALUE = "post_notification";

    private static final long DEBUG_POST_TALK_NOTIFICATION_DELAY_MS = TimeUnit.SECONDS.toMillis(10);
    private static final long PROD_POST_TALK_NOTIFICATION_DELAY_MS = TimeUnit.MINUTES.toMillis(15);

    // TODO Change 10 minutes to proper one!
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

    public boolean scheduleNotification(SlotApiModel slotApiModel, boolean withToast) {
        final NotificationConfiguration cfg = NotificationConfiguration.create(slotApiModel, withToast);
        scheduleNotificationFromConfiguration(cfg);
        return true;
    }

    private void scheduleNotificationFromConfiguration(NotificationConfiguration cfg) {

        // We can't do notification for past events...
        if (!cfg.canScheduleNotification()) {
            Toast.makeText(context,
                    context.getString(R.string.toast_notification_not_set),
                    Toast.LENGTH_SHORT).show();
            return;
        }


        storeConfiguration(cfg);

        scheduleTalkNotificationAlarm(cfg);
        schedulePostNotificationAlarm(cfg);

        if (cfg.isWithToast()) {
            final DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
            final DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
            final String date = dateFormat.format(cfg.getTalkNotificationTime());
            String time = timeFormat.format(cfg.getTalkNotificationTime());

            if (!dateFormat.format(System.currentTimeMillis()).equals(date)) {
                time = "\n" + date + " " + time;
            }

            Toast.makeText(context, context.getString(R.string.toast_notification_set_at) +
                    " " + time, Toast.LENGTH_SHORT).show();
        }
    }

    private void schedulePostNotificationAlarm(NotificationConfiguration cfg) {
        final PendingIntent pendingIntent = createPostNotificationPendingIntent(cfg);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, cfg.getPostTalkNotificationTime(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cfg.getPostTalkNotificationTime(), pendingIntent);
        }
    }

    private PendingIntent createPostNotificationPendingIntent(NotificationConfiguration cfg) {
        final Intent intent = new Intent(context, AlarmReceiver_.class);
        final String slotID = cfg.getSlotId();
        intent.putExtra(EXTRA_TALK_ID, slotID);
        intent.putExtra(EXTRA_NOTIFICATION_TYPE, EXTRA_NOTIFICATION_TYPE_VALUE);
        return PendingIntent.getBroadcast(context, (int) (slotID.hashCode()
                + cfg.getPostTalkNotificationTime()), intent, 0);
    }

    private PendingIntent createTalkPendingIntentToOpenMainActivity(String slotID) {
        final Intent internalIntent = new Intent(context, MainActivity_.class);
        internalIntent.putExtra(EXTRA_TALK_ID, slotID);
        return PendingIntent.getActivity(context,
                slotID.hashCode(), internalIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void removeNotification(String slotid) {
        unscheduleNotification(slotid, true);
    }

    private void unscheduleNotification(String slotId, boolean finishNotification) {
        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        if (finishNotification) {
            // Cancel post-talk notification.
            cancelPostTalkNotificationOnAlarmManager(slotId);

            flagNotificationAsComplete(realm, slotId);
        } else {
            // Cancel talk notification.
            cancelTalkNotificationOnAlarmManager(slotId);

            flagNotificationAsFiredForTalk(realm, slotId);
        }
        realm.commitTransaction();
    }

    private void cancelPostTalkNotificationOnAlarmManager(String slotId) {
        final NotificationConfiguration cfg = getConfiguration(slotId);
        alarmManager.cancel(createPostNotificationPendingIntent(cfg));
    }

    private void cancelTalkNotificationOnAlarmManager(String slotId) {
        final NotificationConfiguration cfg = getConfiguration(slotId);
        alarmManager.cancel(createPendingIntentForAlarmReceiver(cfg));
    }

    private PendingIntent createPendingIntentForAlarmReceiver(NotificationConfiguration cfg) {
        final Intent intent = new Intent(context, AlarmReceiver_.class);
        intent.putExtra(EXTRA_TALK_ID, cfg.getSlotId());
        return PendingIntent.getBroadcast(context, cfg.getSlotId().hashCode(), intent, 0);
    }

    private void flagNotificationAsFiredForTalk(Realm realm, String slotId) {
        final RealmNotification notification = realm.where(RealmNotification.class)
                .equalTo(RealmNotification.Contract.SLOT_ID, slotId).findFirst();
        if (notification != null) {
            notification.setFiredForTalk(true);
        }
    }

    private void flagNotificationAsComplete(Realm realm, String slotId) {
        realm.where(RealmNotification.class).equalTo(RealmNotification.Contract.SLOT_ID,
                slotId).findAll().clear();
    }

    public void showNotificationForVote(String slotId, String title, String desc) {
        final Realm realm = realmProvider.getRealm();
        final RealmNotification realmNotification = realm
                .where(RealmNotification.class)
                .equalTo(RealmNotification.Contract.SLOT_ID, slotId).findFirst();

        if (isNotificationBeforeEvent(realmNotification)) {
            final Notification notification = createPostNotification(
                    title, desc, realmNotification, createTalkPendingIntentToOpenMainActivity(slotId));
            notificationManager.notify(slotId.hashCode(), notification);
        }

        unscheduleNotification(slotId, true);
    }

    public void showNotificationForTalk(String slotId) {
        final Realm realm = realmProvider.getRealm();
        final RealmNotification realmNotification = realm.where(RealmNotification.class)
                .equalTo(RealmNotification.Contract.SLOT_ID, slotId).findFirst();

        if (isNotificationBeforeEvent(realmNotification)) {
            final Notification notification = createTalkNotification(
                    realmNotification, createTalkPendingIntentToOpenMainActivity(slotId));
            notificationManager.notify(slotId.hashCode(), notification);
        }

        unscheduleNotification(slotId, false);
    }

    public boolean isNotificationScheduled(String slotId) {
        final Realm realm = realmProvider.getRealm();
        final RealmNotification notification = realm.where(RealmNotification.class)
                .equalTo(RealmNotification.Contract.SLOT_ID, slotId).findFirst();
        final boolean isNotificationLive = notification != null;
        final boolean result = isNotificationLive && !notification.isFiredForTalk();
        realm.close();
        return result;
    }

    private Notification createPostNotification(
            String title, String desc,
            RealmNotification realmNotification, PendingIntent contentIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(desc);
        notificationBuilder.setContentIntent(contentIntent);
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
        notificationBuilder.setWhen(realmNotification.getTalkTime());
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationBuilder.setTicker(desc);

        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        return notification;
    }

    @NonNull
    private Notification createTalkNotification(RealmNotification realmNotification, PendingIntent contentIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentTitle(realmNotification.getRoomName());
        notificationBuilder.setContentText(realmNotification.getTalkTitle());
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(realmNotification.getTalkTitle()));
        notificationBuilder.setContentIntent(contentIntent);
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
        notificationBuilder.setWhen(realmNotification.getTalkTime());
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationBuilder.setTicker(realmNotification.getTalkTitle());

        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        return notification;
    }

    private boolean isNotificationBeforeEvent(RealmNotification realmNotification) {
        return BuildConfig.DEBUG || realmNotification.getTalkTime()
                > System.currentTimeMillis() - 600000;
    }

    public List<RealmNotification> getAlarms() {
        final Realm realm = realmProvider.getRealm();
        return realm.where(RealmNotification.class).findAll();
    }

    @SuppressLint("Wakelock")
    public void resetAlarms() {
        final PowerManager.WakeLock wakeLock = powerManager.
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlarmService");
        wakeLock.acquire();

        final List<RealmNotification> notificationsList = getAlarms();

        if (!notificationsList.isEmpty()) {
            for (RealmNotification model : notificationsList) {
                final NotificationConfiguration cfg = NotificationConfiguration.create(model);
                cancelPostTalkNotificationOnAlarmManager(cfg.getSlotId());
                cancelTalkNotificationOnAlarmManager(cfg.getSlotId());
                scheduleNotificationFromConfiguration(cfg);
            }
        }

        wakeLock.release();
    }

    private NotificationConfiguration getConfiguration(String slotID) {
        final Realm realm = realmProvider.getRealm();
        final RealmNotification rn = realm.where(RealmNotification.class).equalTo("slotId", slotID).findFirst();
        realm.close();
        return NotificationConfiguration.create(rn);
    }

    private void storeConfiguration(NotificationConfiguration notifyModel) {
        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        final RealmNotification model = new RealmNotification();
        model.setTalkNotificationTime(notifyModel.getTalkNotificationTime());
        model.setTalkTime(notifyModel.getTalkStartTime());
        model.setSlotId(notifyModel.getSlotId());
        model.setRoomName(notifyModel.getRoomName());
        model.setTalkTitle(notifyModel.getTalkTitle());
        realm.copyToRealmOrUpdate(model);
        realm.commitTransaction();
        realm.close();
    }

    private void scheduleTalkNotificationAlarm(NotificationConfiguration cfg) {
        final PendingIntent alarmIntent = createPendingIntentForAlarmReceiver(cfg);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, cfg.getTalkNotificationTime(), alarmIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cfg.getTalkNotificationTime(), alarmIntent);
        }
    }

    static class NotificationConfiguration {
        private final String talkSlotId;
        private final String talkTitle;
        private final String talkRoom;

        private final long talkStartTime;
        private final boolean withToast;

        // Notification for talk.
        private final long talkNotificationTime;

        // Notification for post-talk (eg. voting).
        private final long postTalkNotificationTime;

        public static NotificationConfiguration create(SlotApiModel slotApiModel, boolean withToast) {
            return new NotificationConfiguration(slotApiModel, withToast);
        }

        NotificationConfiguration(
                String talkSlotId, String talkTitle, String talkRoom,
                long talkStartTime, boolean withToast, long talkNotificationTime,
                long postTalkNotificationTime) {
            this.talkSlotId = talkSlotId;
            this.talkTitle = talkTitle;
            this.talkRoom = talkRoom;
            this.talkStartTime = talkStartTime;
            this.withToast = withToast;
            this.talkNotificationTime = talkNotificationTime;
            this.postTalkNotificationTime = postTalkNotificationTime;
        }

        NotificationConfiguration(SlotApiModel slotApiModel, boolean toastInfo) {
            talkSlotId = slotApiModel.slotId;
            talkTitle = slotApiModel.talk.title;
            talkRoom = slotApiModel.roomName;
            talkStartTime = slotApiModel.fromTimeMillis;
            withToast = toastInfo;

            final long beforeTalkNotificationTime = BuildConfig.DEBUG
                    ? DEBUG_BEFORE_TALK_NOTIFICATION_SPAN_MS
                    : PROD_BEFORE_TALK_NOTIFICATION_SPAN_MS;

            // In debug we set talk notification in future for tests...
            talkNotificationTime = BuildConfig.DEBUG
                    ? System.currentTimeMillis() + beforeTalkNotificationTime
                    : talkStartTime - beforeTalkNotificationTime;

            // In debug we set post-talk notification in small amout on time for tests...
            postTalkNotificationTime = talkNotificationTime +
                    (BuildConfig.DEBUG
                            ? DEBUG_POST_TALK_NOTIFICATION_DELAY_MS
                            : PROD_POST_TALK_NOTIFICATION_DELAY_MS);
        }

        public static NotificationConfiguration create(RealmNotification rn) {
            return new NotificationConfiguration(
                    rn.getSlotId(),
                    rn.getTalkTitle(),
                    rn.getRoomName(),
                    rn.getTalkTime(),
                    rn.isWithToast(),
                    rn.getTalkNotificationTime(),
                    rn.getPostNotificationTime()
            );
        }

        public long getTalkNotificationTime() {
            return talkNotificationTime;
        }

        public long getTalkStartTime() {
            return talkStartTime;
        }

        public boolean canScheduleNotification() {
            return BuildConfig.DEBUG || talkNotificationTime > System.currentTimeMillis();
        }

        public boolean isWithToast() {
            return withToast;
        }

        public String getSlotId() {
            return talkSlotId;
        }

        public String getTalkTitle() {
            return talkTitle;
        }

        public String getRoomName() {
            return talkRoom;
        }

        public long getPostTalkNotificationTime() {
            return postTalkNotificationTime;
        }
    }
}

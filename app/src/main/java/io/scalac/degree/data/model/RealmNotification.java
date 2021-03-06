package io.scalac.degree.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmNotification extends RealmObject {

    @PrimaryKey
    private String slotId;
    private String roomName;
    private String talkName;
    private long eventTime;
    private long alarmTime;
    private boolean firedForTalk;

    public boolean isFiredForTalk() {
        return firedForTalk;
    }

    public void setFiredForTalk(boolean firedForTalk) {
        this.firedForTalk = firedForTalk;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public long getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(long alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getTalkName() {
        return talkName;
    }

    public void setTalkName(String talkName) {
        this.talkName = talkName;
    }

    public static class Contract {
        public static final String SLOT_ID = "slotId";
    }
}

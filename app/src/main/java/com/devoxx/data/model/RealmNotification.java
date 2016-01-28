package com.devoxx.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmNotification extends RealmObject {

    @PrimaryKey
    private String slotId;
    private String roomName;
    private String talkTitle;
    private long talkTime;
    private long talkNotificationTime;
    private long postNotificationTime;
    private boolean firedForTalk;
    private boolean withToast;

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

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getTalkTitle() {
        return talkTitle;
    }

    public void setTalkTitle(String talkTitle) {
        this.talkTitle = talkTitle;
    }

    public long getTalkNotificationTime() {
        return talkNotificationTime;
    }

    public void setTalkNotificationTime(long talkNotificationTime) {
        this.talkNotificationTime = talkNotificationTime;
    }

    public long getPostNotificationTime() {
        return postNotificationTime;
    }

    public void setPostNotificationTime(long postNotificationTime) {
        this.postNotificationTime = postNotificationTime;
    }

    public boolean isWithToast() {
        return withToast;
    }

    public void setWithToast(boolean withToast) {
        this.withToast = withToast;
    }

    public long getTalkTime() {
        return talkTime;
    }

    public void setTalkTime(long talkTime) {
        this.talkTime = talkTime;
    }

    public static class Contract {
        public static final String SLOT_ID = "slotId";
    }
}

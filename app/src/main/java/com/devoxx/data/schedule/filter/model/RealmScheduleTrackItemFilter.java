package com.devoxx.data.schedule.filter.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmScheduleTrackItemFilter extends RealmObject {
    @PrimaryKey
    private String trackName;
    private String trackId;
    private boolean isActive;

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String name) {
        this.trackName = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackName) {
        this.trackId = trackName;
    }
}

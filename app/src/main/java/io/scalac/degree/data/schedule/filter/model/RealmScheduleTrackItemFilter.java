package io.scalac.degree.data.schedule.filter.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmScheduleTrackItemFilter extends RealmObject {
    @PrimaryKey
    private String label;
    private String trackName;
    private boolean isActive;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }
}

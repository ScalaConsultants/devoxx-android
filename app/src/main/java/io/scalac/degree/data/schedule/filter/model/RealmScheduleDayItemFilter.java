package io.scalac.degree.data.schedule.filter.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmScheduleDayItemFilter extends RealmObject {
    @PrimaryKey
    private String label;
    private long dayMs;
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

    public long getDayMs() {
        return dayMs;
    }

    public void setDayMs(long dayMs) {
        this.dayMs = dayMs;
    }
}

package io.scalac.degree.data.conference.model;

import java.io.Serializable;

public class ConferenceDay implements Serializable {

    private String name;
    private long dayMs;

    public ConferenceDay(long day, String dayName) {
        this.dayMs = day;
        this.name = dayName;
    }

    public String getName() {
        return name;
    }

    public long getDayMs() {
        return dayMs;
    }

    @Override
    public String toString() {
        return "io.scalac.degree.data.conference.model.ConferenceDay{" +
                "dayMs=" + dayMs +
                ", name='" + name + '\'' +
                '}';
    }
}

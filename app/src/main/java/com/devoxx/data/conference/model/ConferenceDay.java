package com.devoxx.data.conference.model;

import java.io.Serializable;

public class ConferenceDay implements Serializable {

    private String name;
    private long dayMs;
    private boolean isRunning;

    public ConferenceDay(long day, String dayName, boolean running) {
        this.dayMs = day;
        this.name = dayName;
        this.isRunning = running;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public String getName() {
        return name;
    }

    public long getDayMs() {
        return dayMs;
    }


    @Override
    public String toString() {
        return "ConferenceDay{" +
                "dayMs=" + dayMs +
                ", isRunning=" + isRunning +
                ", name='" + name + '\'' +
                ", running=" + isRunning() +
                '}';
    }
}

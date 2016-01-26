package com.devoxx.data.cache.model;

import io.realm.RealmObject;

public class CacheSlotsObject extends RealmObject {
    private String rawData;
    private long timestamp;

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

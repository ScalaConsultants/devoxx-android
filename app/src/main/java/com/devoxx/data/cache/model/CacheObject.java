package com.devoxx.data.cache.model;

import io.realm.RealmObject;

public class CacheObject extends RealmObject {

    private long timestamp;
    private String query;
    private String rawData;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

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

    public static class Contract {
        public static final String QUERY = "query";
    }
}

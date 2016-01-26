package com.devoxx.data.model;

import io.realm.RealmObject;

public class RealmSlotsAggregate extends RealmObject {

    private String rawData;

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }
}

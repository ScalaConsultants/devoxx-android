package com.devoxx.data.model;

import com.devoxx.connection.cfp.model.FloorApiModel;

import io.realm.RealmObject;

public class RealmFloor extends RealmObject {
    private String img;
    private String title;
    private String tabpos;
    private String target;

    public RealmFloor() {
        // Default.
    }

    public RealmFloor(FloorApiModel floor) {
        img = floor.img;
        title = floor.title;
        tabpos = floor.tabpos;
        target = floor.target;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTabpos() {
        return tabpos;
    }

    public void setTabpos(String tabpos) {
        this.tabpos = tabpos;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}

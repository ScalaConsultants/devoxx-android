package com.devoxx.data.model;

import com.devoxx.connection.model.TrackApiModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmTrack extends RealmObject {

    @PrimaryKey
    private String id;
    private String imgsrc;
    private String title;
    private String description;

    public static RealmTrack createFromApi(TrackApiModel apiModel) {
        final RealmTrack result = new RealmTrack();
        result.setDescription(apiModel.description);
        result.setId(apiModel.id);
        result.setImgsrc(apiModel.imgsrc);
        result.setTitle(apiModel.title);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Contract {
        public static final String TITLE = "title";
        public static final String ID = "id";
    }
}

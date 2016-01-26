package com.devoxx.connection.model;

import com.devoxx.data.model.RealmTalk;

import java.util.List;

public class TalkShortApiModel extends TalkBaseApiModel {
    public List<LinkApiModel> links;

    public static TalkShortApiModel fromDb(RealmTalk dbModel) {
        final TalkShortApiModel result = new TalkShortApiModel();
        result.id = dbModel.getId();
        result.title = dbModel.getTitle();
        result.talkType = dbModel.getTalkType();
        result.track = dbModel.getTrack();
        return result;
    }
}

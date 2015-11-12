package io.scalac.degree.connection.model;

import java.util.List;

import io.scalac.degree.data.model.TalkDbModel;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 26/10/2015
 */
public class TalkShortApiModel extends TalkBaseApiModel {
    public List<LinkApiModel> links;

    public static TalkShortApiModel fromDb(TalkDbModel dbModel) {
        final TalkShortApiModel result = new TalkShortApiModel();
        result.id = dbModel.getId();
        result.title = dbModel.getTitle();
        result.talkType = dbModel.getTalkType();
        result.track = dbModel.getTrack();
        return result;
    }
}

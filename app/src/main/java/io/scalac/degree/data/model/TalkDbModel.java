package io.scalac.degree.data.model;

import io.realm.Realm;
import io.realm.RealmObject;
import io.scalac.degree.connection.model.TalkShortApiModel;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 30/10/2015
 */
public class TalkDbModel extends RealmObject {
	private String title;
	private String talkType;
	private String track;
	private String id;

	public static TalkDbModel fromApiModel(TalkShortApiModel apiModel) {
		final TalkDbModel result = new TalkDbModel();
		result.setId(apiModel.id);
		result.setTalkType(apiModel.talkType);
		result.setTitle(apiModel.title);
		result.setTrack(apiModel.track);
		return result;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTalkType() {
		return talkType;
	}

	public void setTalkType(String talkType) {
		this.talkType = talkType;
	}

	public String getTrack() {
		return track;
	}

	public void setTrack(String track) {
		this.track = track;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}

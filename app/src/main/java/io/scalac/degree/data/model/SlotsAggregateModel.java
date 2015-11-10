package io.scalac.degree.data.model;

import io.realm.RealmObject;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 05/11/2015
 */
public class SlotsAggregateModel extends RealmObject {

	private String rawData;

	public String getRawData() {
		return rawData;
	}

	public void setRawData(String rawData) {
		this.rawData = rawData;
	}
}

package io.scalac.degree.connection.model;

import java.util.List;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 26/10/2015
 */
public class TalkFullApiModel extends TalkBaseApiModel {
	public String summaryAsHtml;
	public String lang;
	public String summary;
	public List<TalkSpeakerApiModel> speakers;
}

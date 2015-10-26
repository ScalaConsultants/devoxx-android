package io.scalac.degree.connection.model;

import java.util.List;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 26/10/2015
 */
public class SpeakerFullApiModel extends SpeakerBaseApiModel {
	public String bio;
	public String bioAsHtml;
	public String company;
	public String blog;
	public String twitter;
	public String lang;
	public List<LinkApiModel> links;
	public List<TalkShortApiModel> acceptedTalks;
}

package io.scalac.degree.connection.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.List;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 26/10/2015
 */
public class ConferencesApiModel implements Serializable {
	public String content;
	public List<LinkApiModel> links;

	public static String extractConferenceCode(LinkApiModel linkApiModel) {
		return Uri.parse(linkApiModel.href).getLastPathSegment();
	}
}

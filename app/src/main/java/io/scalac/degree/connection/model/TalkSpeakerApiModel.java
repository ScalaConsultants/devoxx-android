package io.scalac.degree.connection.model;

import android.net.Uri;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 26/10/2015
 */
public class TalkSpeakerApiModel extends SpeakerBaseApiModel {
    public String name;
    public LinkApiModel link;

    public static String getUuidFromLink(LinkApiModel link) {
        return Uri.parse(link.href).getLastPathSegment();
    }
}

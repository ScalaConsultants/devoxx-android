package io.scalac.degree.connection.model;

import android.net.Uri;

public class TalkSpeakerApiModel extends SpeakerBaseApiModel {
    public String name;
    public LinkApiModel link;

    public static String getUuidFromLink(LinkApiModel link) {
        return Uri.parse(link.href).getLastPathSegment();
    }
}

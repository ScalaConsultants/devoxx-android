package com.devoxx.connection.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.List;

public class ConferencesApiModel implements Serializable {
    public final String content;
    public final List<LinkApiModel> links;

    public ConferencesApiModel(String content, List<LinkApiModel> links) {
        this.content = content;
        this.links = links;
    }

    public static String extractConferenceCode(LinkApiModel linkApiModel) {
        return Uri.parse(linkApiModel.href).getLastPathSegment();
    }
}

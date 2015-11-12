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

    public String getReadableSpeakers() {
        final StringBuilder sb = new StringBuilder();
        final int size = speakers.size();
        for (int i = 0; i < size; i++) {
            sb.append(speakers.get(i).name);

            if (i != size - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}

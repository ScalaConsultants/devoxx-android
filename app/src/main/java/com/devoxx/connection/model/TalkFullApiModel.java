package com.devoxx.connection.model;

import java.util.List;

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

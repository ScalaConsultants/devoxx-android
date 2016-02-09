package com.devoxx.connection.model;

import com.devoxx.utils.LazyField;

import java.util.List;

public class TalkFullApiModel extends TalkBaseApiModel {
    public String summaryAsHtml;
    public String lang;
    public String summary;
    public List<TalkSpeakerApiModel> speakers;

    private transient LazyField<String> lazySpeakersReadable = new LazyField<>(this::createSpeakersReadable);

    public String getReadableSpeakers() {
        return lazySpeakersReadable.getValue();
    }

    private String createSpeakersReadable() {
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

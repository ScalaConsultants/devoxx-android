package com.devoxx.connection.model;

import java.util.List;

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

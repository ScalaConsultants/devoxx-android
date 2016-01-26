package com.devoxx.connection.vote.model;

import java.util.ArrayList;
import java.util.List;

public class VoteTalkModel extends VoteTalkSimpleModel {
    public String title;
    public String summary;
    public String type;
    public String track;
    public List<String> speakers;

    public static VoteTalkModel createFake(String count) {
        final VoteTalkModel result = new VoteTalkModel();
        result.title = "Fake talk";
        result.summary = "Fake talk";
        result.type = "Fake talk";
        result.name = "Fake talk";
        result.track = "Fake talk";
        result.speakers = new ArrayList<>(0);
        result.count = count;
        result.avg = "4.6";
        result.sum = "132";
        return result;
    }
}

package com.devoxx.data.vote;

import io.realm.RealmObject;

public class VotedTalkModel extends RealmObject {
    private String talkId;

    public VotedTalkModel() {
        // Default.
    }

    public VotedTalkModel(String talkId) {
        this.talkId = talkId;
    }

    public String getTalkId() {
        return talkId;
    }

    public void setTalkId(String talkId) {
        this.talkId = talkId;
    }
}

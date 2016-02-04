package com.devoxx.connection.vote.model;

import java.io.Serializable;

public class VoteApiModel implements Serializable {
    public String talkId;
    public String user;
    public int rating;

    public VoteApiModel(String talkId, int rating, String user) {
        this.talkId = talkId;
        this.rating = rating;
        this.user = user;
    }
}

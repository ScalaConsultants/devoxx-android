package com.devoxx.data.vote.interfaces;

import com.devoxx.connection.vote.model.VoteTalkModel;

public interface IOnGetTalkVotesListener {
    void onTalkVotesAvailable(VoteTalkModel voteTalkModel);

    void onTalkVotesError();
}

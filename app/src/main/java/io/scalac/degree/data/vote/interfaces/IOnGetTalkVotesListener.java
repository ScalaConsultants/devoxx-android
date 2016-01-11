package io.scalac.degree.data.vote.interfaces;

import io.scalac.degree.connection.vote.model.VoteTalkModel;

public interface IOnGetTalkVotesListener {
    void onTalkVotesAvailable(VoteTalkModel voteTalkModel);

    void onTalkVotesError();
}

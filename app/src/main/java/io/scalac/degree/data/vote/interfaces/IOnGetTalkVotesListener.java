package io.scalac.degree.data.vote.interfaces;

import io.scalac.degree.connection.vote.model.VoteTalkModel;

/**
 * Jacek Modrakowski
 * modrakowski.pl
 * 08/12/2015.
 */
public interface IOnGetTalkVotesListener {
    void onTalkVotesAvailable(VoteTalkModel voteTalkModel);

    void onTalkVotesError();
}

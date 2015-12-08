package io.scalac.degree.data.vote.interfaces;

/**
 * scalac.io
 * jacek.modrakowski@scalac.io
 * 08/12/2015.
 */
public interface ITalkVoter {

    void voteForTalk(String talkId, IOnVoteForTalkListener listener);

    void getVotesCountForTalk(String talkId, IOnGetTalkVotesListener listener);
}

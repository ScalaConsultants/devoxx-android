package io.scalac.degree.data.vote.interfaces;

public interface ITalkVoter {

    boolean isVotingEnabled();

    void voteForTalk(String talkId, IOnVoteForTalkListener listener);

    void getVotesCountForTalk(String talkId, IOnGetTalkVotesListener listener);
}

package com.devoxx.data.vote.interfaces;

public interface ITalkVoter {

    boolean isVotingEnabled();

    void voteForTalk(String confCode, String talkId, IOnVoteForTalkListener listener);

    void getVotesCountForTalk(String confCode, String talkId, IOnGetTalkVotesListener listener);
}

package com.devoxx.data.vote.interfaces;

import android.content.Context;

public interface ITalkVoter {

    boolean isVotingEnabled();

    void voteForTalk(String talkId, IOnVoteForTalkListener listener);

    void getVotesCountForTalk(String confCode, String talkId, IOnGetTalkVotesListener listener);

    void showVoteDialog(Context context, String talkId, IOnVoteForTalkListener listener);
}

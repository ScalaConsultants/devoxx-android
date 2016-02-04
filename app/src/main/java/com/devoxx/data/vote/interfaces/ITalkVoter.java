package com.devoxx.data.vote.interfaces;

import android.content.Context;

public interface ITalkVoter {

    boolean isVotingEnabled();

    boolean canVoteOnTalk(String talkId);

    void showVoteDialog(Context context, String talkId, IOnVoteForTalkListener listener);
}

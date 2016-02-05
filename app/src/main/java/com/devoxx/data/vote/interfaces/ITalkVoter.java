package com.devoxx.data.vote.interfaces;

import android.content.Context;

import com.devoxx.connection.model.SlotApiModel;

public interface ITalkVoter {

    boolean isVotingEnabled();

    boolean canVoteOnTalk(String talkId);

    void showVoteDialog(Context context, SlotApiModel slot, IOnVoteForTalkListener listener);
}

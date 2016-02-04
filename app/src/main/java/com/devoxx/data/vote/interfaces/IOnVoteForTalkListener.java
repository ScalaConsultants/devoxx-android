package com.devoxx.data.vote.interfaces;

public interface IOnVoteForTalkListener {
    void onVoteForTalkSucceed();

    void onVoteForTalkFailed(Exception e);

    void onCantVoteOnTalkYet();

    void onCantVoteMoreThanOnce();
}

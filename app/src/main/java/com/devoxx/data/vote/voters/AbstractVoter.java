package com.devoxx.data.vote.voters;

import com.devoxx.data.vote.interfaces.IOnVoteForTalkListener;
import com.devoxx.data.vote.interfaces.ITalkVoter;

import android.content.Context;
import android.text.TextUtils;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import com.devoxx.data.Settings_;

@EBean
public abstract class AbstractVoter implements ITalkVoter {

    @Pref
    Settings_ settings;

    @Override
    public boolean isVotingEnabled() {
        return !TextUtils.isEmpty(settings.userId().getOr(""));
    }

    @Override
    public void showVoteDialog(Context context, String talkId, IOnVoteForTalkListener listener) {
        // Nothing here.
    }
}

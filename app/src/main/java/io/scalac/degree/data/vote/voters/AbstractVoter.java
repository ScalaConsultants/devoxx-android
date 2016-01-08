package io.scalac.degree.data.vote.voters;

import android.text.TextUtils;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import io.scalac.degree.data.Settings_;
import io.scalac.degree.data.vote.interfaces.ITalkVoter;

@EBean
public abstract class AbstractVoter implements ITalkVoter {

    @Pref
    Settings_ settings;

    @Override
    public boolean isVotingEnabled() {
        return !TextUtils.isEmpty(settings.userId().getOr(""));
    }
}

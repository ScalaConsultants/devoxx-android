package com.devoxx.data;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface Settings {

    @DefaultString("")
    String userId();

    @DefaultBoolean(false)
    boolean filterTalksBySchedule();

    @DefaultBoolean(false)
    boolean requestedConferenceChange();
}

package com.devoxx.data;

import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface DataInformation {

    @DefaultLong(0L) long lastSpeakersCall();
}

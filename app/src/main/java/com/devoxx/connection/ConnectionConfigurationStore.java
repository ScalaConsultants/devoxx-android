package com.devoxx.connection;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface ConnectionConfigurationStore {

    @DefaultString("")
    String activeConferenceApiUrl();
}

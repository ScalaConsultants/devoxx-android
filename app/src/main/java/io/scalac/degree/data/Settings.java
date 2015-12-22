package io.scalac.degree.data;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface Settings {

    @DefaultString("")
    String userId();

    @DefaultBoolean(false)
    boolean filterTalksBySchedule();
}

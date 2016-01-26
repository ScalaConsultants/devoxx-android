package com.devoxx.data.schedule.search;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface ScheduleSearchStore {

    @DefaultString("")
    String lastQuery();
}

package com.devoxx.navigation;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface NavigationHelper {

    @DefaultBoolean(false)
    boolean isUpdateNeeded();
}

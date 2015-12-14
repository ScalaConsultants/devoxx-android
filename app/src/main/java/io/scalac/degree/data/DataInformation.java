package io.scalac.degree.data;

import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Jacek Modrakowski
 * modrakowski.pl
 * 11/12/2015.
 */
@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface DataInformation {

    @DefaultLong(0L) long lastSpeakersCall();
}

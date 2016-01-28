package com.devoxx.android.fragment.settings;

import android.content.Intent;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;

import com.devoxx.R;
import com.devoxx.android.activity.SelectorActivity_;
import com.devoxx.data.conference.ConferenceManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.PreferenceClick;

@EFragment
public class SettingsFragment extends PreferenceFragment {

    @Bean
    ConferenceManager conferenceManager;

    @AfterViews
    void afterViews() {
        addPreferencesFromResource(R.xml.preferences);
    }

    @PreferenceClick(R.string.settings_change_conf_key)
    void onChangeConferenceClick() {
        conferenceManager.clearCurrentConferenceData();

        ActivityCompat.finishAffinity(getActivity());

        SelectorActivity_.intent(this)
                .flags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .start();
    }
}

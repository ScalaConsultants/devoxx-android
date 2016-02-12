package com.devoxx.android.activity;


import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.devoxx.R;
import com.devoxx.data.conference.ConferenceManager;
import com.devoxx.data.model.RealmConference;
import com.devoxx.navigation.Navigator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_about)
public class AboutActivity extends BaseActivity {

    @Bean
    ConferenceManager conferenceManager;

    @Bean
    Navigator navigator;

    @ViewById(R.id.main_toolbar)
    Toolbar toolbar;

    @ViewById(R.id.main_appbar)
    AppBarLayout appBarLayout;

    @ViewById(R.id.main_collapsing)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @ViewById(R.id.aboutWebButton)
    FloatingActionButton firstButton;

    @ViewById(R.id.aboutTwitterButton)
    FloatingActionButton secondButton;

    @ViewById(R.id.aboutDescription)
    TextView description;

    @AfterViews
    void afterViews() {
        collapsingToolbarLayout.setTitle(getString(R.string.about));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        description.setText(conferenceManager.getActiveConference().getDescription());
    }

    @OptionsItem(android.R.id.home)
    void onBackClick() {
        finish();
    }

    @Click(R.id.aboutWebButton)
    void onWebButtonClick() {
        final String www = conferenceManager.getActiveConference().getWwwURL();
        navigator.openWwwLink(this, www);
    }

    @Click(R.id.aboutTwitterButton)
    void onTwitterButtonClick() {
        final RealmConference conference = conferenceManager.getActiveConference();
        final String message = String.format("Check Devoxx %s conference! %s",
                conference.getCountry(),
                conference.getWwwURL());
        navigator.tweetMessage(this, message);
    }
}

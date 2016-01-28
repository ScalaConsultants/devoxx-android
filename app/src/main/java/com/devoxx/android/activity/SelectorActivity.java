package com.devoxx.android.activity;

import android.view.View;
import android.widget.ProgressBar;

import com.devoxx.R;
import com.devoxx.android.view.selector.SelectorView;
import com.devoxx.connection.Connection;
import com.devoxx.connection.cfp.model.ConferenceApiModel;
import com.devoxx.connection.vote.VoteConnection;
import com.devoxx.data.Settings_;
import com.devoxx.data.conference.ConferenceManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.List;

@EActivity(R.layout.activity_selector)
public class SelectorActivity extends BaseActivity implements ConferenceManager.IConferencesListener,
        ConferenceManager.IConferenceDataListener, SelectorView.IWheelItemActionListener {

    @Bean
    ConferenceManager conferenceManager;

    @Bean
    Connection connection;

    @Bean
    VoteConnection voteConnection;

    @Pref
    Settings_ settings;

    @ViewById(R.id.homeProgressBar)
    ProgressBar progressBar;

    @ViewById(R.id.selectorWheel)
    SelectorView selectorView;

    private ConferenceApiModel lastSelectedConference;

    @AfterViews
    void afterViews() {
        if (conferenceManager.isConferenceChoosen()) {
            navigateToHome();
            finish();
        } else {
            conferenceManager.fetchAvailableConferences(this);
            selectorView.setListener(this);
        }
    }

    @Click(R.id.selectorGo)
    void onGoClick() {
        final String confCode = lastSelectedConference.id;
        settings.edit().activeConferenceCode().put(confCode).apply();

        connection.setupConferenceApi(lastSelectedConference.cfpURL);
        voteConnection.setupApi(lastSelectedConference.votingURL);
        conferenceManager.fetchConferenceData(lastSelectedConference, this);
    }

    public void showLoader() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoader() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void navigateToHome() {
        MainActivity_.intent(this).start();
        finish();
    }

    @Override
    public void onConferencesDataStart() {
        showLoader();
    }

    @Override
    public void onConferencesAvailable(List<ConferenceApiModel> conferences) {
        for (ConferenceApiModel conference : conferences) {
            selectorView.addNewItem(conference);
        }
        hideLoader();
    }

    @Override
    public void onConferencesError() {
        hideLoader();
    }

    @Override
    public void onConferenceDataStart() {
        showLoader();
    }

    @Override
    public void onConferenceDataAvailable() {
        hideLoader();
        navigateToHome();
    }

    @Override
    public void onConferenceDataError() {
        hideLoader();
    }

    @Override
    public void onWheelItemSelected(ConferenceApiModel data) {
        lastSelectedConference = data;
    }

    @Override
    public void onWheelItemClicked(ConferenceApiModel data) {
        // Nothing here.
    }
}

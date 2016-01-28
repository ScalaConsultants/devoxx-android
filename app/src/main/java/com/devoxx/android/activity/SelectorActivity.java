package com.devoxx.android.activity;

import android.app.ActivityManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devoxx.R;
import com.devoxx.android.view.selector.SelectorView;
import com.devoxx.connection.Connection;
import com.devoxx.connection.cfp.model.ConferenceApiModel;
import com.devoxx.connection.vote.VoteConnection;
import com.devoxx.data.Settings_;
import com.devoxx.data.conference.ConferenceManager;
import com.devoxx.data.model.RealmConference;
import com.devoxx.utils.ActivityUtils;
import com.devoxx.utils.Logger;

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

    @Bean
    ActivityUtils activityUtils;

    @Pref
    Settings_ settings;

    @ViewById(R.id.homeProgressBar)
    ProgressBar progressBar;

    @ViewById(R.id.selectorWheel)
    SelectorView selectorView;

    @ViewById(R.id.selectorGo)
    View goButton;

    @ViewById(R.id.selectorCurrentConference)
    TextView currentConferenceLabel;

    private ConferenceApiModel lastSelectedConference;

    @Override
    protected void onResume() {
        super.onResume();

        if (conferenceManager.isConferenceChoosen()) {
            final RealmConference conference = conferenceManager.getActiveConference();
            setupRequiredApis(conference.getCfpURL(), conference.getVotingURL());
            navigateToHome();
            finish();
        } else {
            conferenceManager.fetchAvailableConferences(this);
            selectorView.setListener(this);
        }
    }

    @Click(R.id.selectorGo)
    void onGoClick() {
        setupRequiredApis(lastSelectedConference.cfpURL,
                lastSelectedConference.votingURL);
        conferenceManager.fetchConferenceData(lastSelectedConference, this);
    }

    public void showLoader() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoader() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void navigateToHome() {
        Logger.l("SelectorActivity.navigateToHome");

        MainActivity_.intent(this).start();
        finish();
    }

    @Override
    public void onConferencesDataStart() {
        showLoader();
    }

    @Override
    public void onConferencesAvailable(List<ConferenceApiModel> conferences) {
        selectorView.prepareForConferences(conferences);
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
        Logger.l("SelectorActivity.onConferenceDataAvailable");

        if (activityUtils.isAppForeground(this)) {
            hideLoader();
            navigateToHome();
        }
    }

    @Override
    public void onConferenceDataError() {
        hideLoader();
    }

    @Override
    public void onWheelItemSelected(ConferenceApiModel data) {
        currentConferenceLabel.setText(data.country);
        lastSelectedConference = data;
        goButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onWheelItemClicked(ConferenceApiModel data) {
        goButton.setVisibility(View.GONE);
    }

    private void setupRequiredApis(String cfpUrl, String votingUrl) {
        connection.setupConferenceApi(cfpUrl);
        voteConnection.setupApi(votingUrl);
    }
}

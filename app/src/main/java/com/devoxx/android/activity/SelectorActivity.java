package com.devoxx.android.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devoxx.R;
import com.devoxx.connection.Connection;
import com.devoxx.connection.cfp.model.ConferenceApiModel;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.connection.vote.VoteConnection;
import com.devoxx.data.Settings_;
import com.devoxx.data.conference.ConferenceManager;
import com.devoxx.data.manager.AbstractDataManager;
import com.devoxx.data.manager.SlotsDataManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.List;

@EActivity(R.layout.activity_selector)
public class SelectorActivity extends BaseActivity implements
        AbstractDataManager.IDataManagerListener<SlotApiModel>,
        ConferenceManager.IOnConferencesAvailableListener {

    @Bean
    SlotsDataManager slotsDataManager;

    @Bean
    ConferenceManager conferenceManager;

    @Pref
    Settings_ settings;

    @ViewById(R.id.homeProgressBar)
    ProgressBar progressBar;

    @ViewById(R.id.conferencesChooser)
    LinearLayout container;

    @Bean
    Connection connection;

    @Bean
    VoteConnection voteConnection;

    @AfterViews
    void afterViews() {
        conferenceManager.fetchAvailableConferences(this);
    }

    @Override
    public void onDataStartFetching() {
        showLoader();
    }

    @Override
    public void onDataAvailable(List<SlotApiModel> items) {
        hideLoader();
        navigateToHome();
    }

    @Override
    public void onDataAvailable(SlotApiModel item) {
        // Nothing here.
    }

    @Override
    public void onDataError() {
        hideLoader();
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
    public void onConferencesAvailable(List<ConferenceApiModel> conferenceS) {
        // TODO Dev list.
        for (ConferenceApiModel conferenceApiModel : conferenceS) {
            final TextView textView = new TextView(this);
            textView.setText(conferenceApiModel.cfpURL);
            textView.setOnClickListener(v -> {
                // TODO Back with proper ID.
                final String confCode = "DV15"; //conferenceApiModel.id;
                settings.edit().activeConferenceCode().put(confCode).apply();

                connection.setupConferenceApi(conferenceApiModel.cfpURL);
                voteConnection.setupApi(conferenceApiModel.votingURL);

                conferenceManager.fetchConferenceData(conferenceApiModel);
                slotsDataManager.fetchTalks(confCode, SelectorActivity.this);
            });

            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            container.addView(textView, lp);
        }
    }

    @Override
    public void onConferencesError() {
        // TODO Do something.
    }
}

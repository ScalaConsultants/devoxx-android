package com.devoxx.android.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devoxx.R;
import com.devoxx.android.view.selector.SelectorView;
import com.devoxx.connection.Connection;
import com.devoxx.connection.cfp.model.ConferenceApiModel;
import com.devoxx.connection.vote.VoteConnection;
import com.devoxx.data.Settings_;
import com.devoxx.data.conference.ConferenceManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.List;

@EActivity(R.layout.activity_selector)
public class SelectorActivity extends BaseActivity implements ConferenceManager.IConferencesListener,
        ConferenceManager.IConferenceDataListener {

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

    @ViewById(R.id.conferencesChooser)
    LinearLayout container;

    @ViewById(R.id.selectorWheel)
    SelectorView selectorView;

    @AfterViews
    void afterViews() {
        // TODO Create items from conferences!
        conferenceManager.fetchAvailableConferences(this);
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
        hideLoader();

        // TODO Dev list.
        for (ConferenceApiModel conferenceApiModel : conferences) {
            selectorView.addNewItem(conferenceApiModel);

            final TextView textView = new TextView(this);
            textView.setText(conferenceApiModel.cfpURL);
            textView.setOnClickListener(v -> {
                final String confCode = conferenceApiModel.id;
                settings.edit().activeConferenceCode().put(confCode).apply();

                connection.setupConferenceApi(conferenceApiModel.cfpURL);
                voteConnection.setupApi(conferenceApiModel.votingURL);
                conferenceManager.fetchConferenceData(conferenceApiModel, this);
            });

            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            container.addView(textView, lp);
        }
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
}

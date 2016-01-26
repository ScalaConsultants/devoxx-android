package com.devoxx.android.activity;

import com.devoxx.data.manager.AbstractDataManager;
import com.devoxx.data.manager.SlotsDataManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.data.Settings_;
import com.devoxx.data.conference.ConferenceManager;

import com.devoxx.R;

@EActivity(R.layout.activity_selector)
public class SelectorActivity extends BaseActivity implements
        AbstractDataManager.IDataManagerListener<SlotApiModel> {

    private static final String TEST_CONF_CODE = "DV15";

    @Bean
    SlotsDataManager slotsDataManager;

    @Bean
    ConferenceManager conferenceManager;

    @Pref
    Settings_ settings;

    @ViewById(R.id.homeProgressBar)
    ProgressBar progressBar;

    @Click(R.id.selectorGo)
    void onGoClick() {
        settings.activeConferenceCode().put(TEST_CONF_CODE);

        // TODO Make conference manager as downloader data for conference!
        conferenceManager.fetchConferenceData(TEST_CONF_CODE);

        slotsDataManager.fetchTalks(settings.activeConferenceCode().get(), this);
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
}

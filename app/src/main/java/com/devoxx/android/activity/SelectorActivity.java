package com.devoxx.android.activity;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Optional;
import com.bumptech.glide.Glide;
import com.devoxx.R;
import com.devoxx.android.view.selector.SelectorValues;
import com.devoxx.android.view.selector.SelectorView;
import com.devoxx.connection.Connection;
import com.devoxx.connection.cfp.model.ConferenceApiModel;
import com.devoxx.connection.vote.VoteConnection;
import com.devoxx.data.Settings_;
import com.devoxx.data.conference.ConferenceManager;
import com.devoxx.data.model.RealmConference;
import com.devoxx.utils.BlurTransformation;
import com.devoxx.utils.FontUtils;
import com.devoxx.utils.InfoUtil;
import com.devoxx.utils.ViewUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Locale;

@EActivity(R.layout.activity_selector)
public class SelectorActivity extends BaseActivity implements ConferenceManager.IConferencesListener,
        ConferenceManager.IConferenceDataListener, SelectorView.IWheelItemActionListener {

    private static final String LAST_CONFERENCE_KEY = "last_clicked_conference";

    @Bean
    ConferenceManager conferenceManager;

    @Bean
    Connection connection;

    @Bean
    VoteConnection voteConnection;

    @Bean
    FontUtils fontUtils;

    @Bean
    ViewUtils viewUtils;

    @Pref
    Settings_ settings;

    @ViewById(R.id.selectorMainContainerImage)
    ImageView mainImage;

    @ViewById(R.id.selectorWheel)
    SelectorView selectorView;

    @ViewById(R.id.selectorGo)
    TextView goButton;

    @ViewById(R.id.selectorCurrentConference)
    TextView currentConferenceLabel;

    @ViewById(R.id.selectorCurrentConferenceInfo)
    TextView confInfo;

    @ViewById(R.id.selectorMainContainer)
    View mainContainer;

    @ViewById(R.id.selectorDaysLeft)
    SelectorValues daysLeft;

    @ViewById(R.id.selectorProposals)
    SelectorValues talks;

    @ViewById(R.id.selectorRegistrations)
    SelectorValues capacity;

    private ConferenceApiModel lastSelectedConference;

    @AfterViews
    void afterViews() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final int statusBarHeight = viewUtils.getStatusBarHeight();
            mainContainer.setPadding(mainContainer.getPaddingLeft(), statusBarHeight,
                    mainContainer.getPaddingRight(), mainContainer.getPaddingBottom());
        }

        fontUtils.applyTypeface(currentConferenceLabel, FontUtils.Font.REGULAR);
        fontUtils.applyTypeface(goButton, FontUtils.Font.REGULAR);
        fontUtils.applyTypeface(confInfo, FontUtils.Font.REGULAR);

        setupImageColorFilter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        final boolean isLoadingData = conferenceManager.registerConferenceDataListener(this);
        conferenceManager.registerAllConferencesDataListener(this);

        if (conferenceManager.isConferenceChoosen()
                && !conferenceManager.requestedChangeConference()) {
            final Optional<RealmConference> conference = conferenceManager.getActiveConference();
            if (conference.isPresent()) {
                setupRequiredApis(conference.get().getCfpURL(),
                        conference.get().getVotingURL());
            }

            conferenceManager.updateSlotsIfNeededInBackground();

            navigateToHome();
            finish();
        } else if (isLoadingData) {
            selectorView.hideIcons();
            selectorView.showProgress();
            hideGoButtonForce();
//            TODO Load image properly from model!
//            loadBackgroundImage(lastSelectedConference.splashImgURL);
        } else {
            if (!connection.isOnline()) {
                conferenceManager.initWitStaticData();
            }

            conferenceManager.fetchAvailableConferences();
            selectorView.setListener(this);
        }
    }

    @Override
    protected void onStop() {
        conferenceManager.unregisterConferenceDataListener();
        conferenceManager.unregisterAllConferencesDataListener();

        super.onStop();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        lastSelectedConference = (ConferenceApiModel) savedInstanceState
                .getSerializable(LAST_CONFERENCE_KEY);
        setupConfInfo(lastSelectedConference);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(LAST_CONFERENCE_KEY, lastSelectedConference);
        super.onSaveInstanceState(outState);
    }

    @Click(R.id.selectorGo)
    void onGoClick() {
        if (connection.isOnline()) {
            if (!conferenceManager.isLastSelectedConference(lastSelectedConference)) {
                conferenceManager.clearCurrentConferenceData();
            }

            setupRequiredApis(lastSelectedConference.cfpURL, lastSelectedConference.votingURL);
            conferenceManager.fetchConferenceData(lastSelectedConference);
        } else if (conferenceManager.isLastSelectedConference(lastSelectedConference)) {
            conferenceManager.openLastConference();
        } else {
            infoUtil.showToast(R.string.no_internet_connection);
        }
    }

    private void hideGoButtonForce() {
        goButton.clearAnimation();
        goButton.setScaleY(0f);
        goButton.setScaleX(0f);
    }

    private void hideGoButton() {
        goButton.clearAnimation();
        goButton.animate().scaleY(0f).scaleX(0f)
                .setInterpolator(new AnticipateInterpolator(1.5f))
                .setDuration(150).start();
    }

    private void showGoButton() {
        goButton.clearAnimation();
        goButton.animate().scaleY(1f).scaleX(1f)
                .setInterpolator(new OvershootInterpolator(1.5f))
                .setDuration(150).start();
    }

    @Bean
    InfoUtil infoUtil;

    @Click(R.id.selectorRegistrations)
    void onCapacityClick() {
        infoUtil.showToast("Go to registration...");
    }

    private void navigateToHome() {
        MainActivity_.intent(this).start();
        finish();
    }

    @Override
    public void onConferencesDataStart() {
        // TODO
    }

    @Override
    public void onConferencesAvailable(List<ConferenceApiModel> conferences) {
        for (ConferenceApiModel conference : conferences) {
            Glide.with(this).load(conference.splashImgURL).preload();
        }

        selectorView.prepareForConferences(conferences);

        if (lastSelectedConference != null) {
            selectorView.restorePreviousStateIfAny(lastSelectedConference);
        } else {
            selectorView.defaultSelection();
        }

        showGoButton();
    }

    @Override
    public void onConferencesError() {
        conferenceManager.initWitStaticData();
        conferenceManager.fetchAvailableConferences();
    }

    @Override
    public void onConferenceDataStart() {
        selectorView.hideIcons();
        selectorView.showProgress();
        hideGoButton();
    }

    @Override
    public void onConferenceDataAvailable(boolean isAnyTalks) {
        if (isAnyTalks) {
            navigateToHome();
        } else {
            showGoButton();
            selectorView.hideProgress();
            selectorView.showIcons();
            infoUtil.showToast(R.string.no_data_available);
        }
    }

    @Override
    public void onConferenceDataError() {
        showGoButton();
        selectorView.hideProgress();
        selectorView.showIcons();
        infoUtil.showToast(R.string.something_went_wrong);
    }

    @Override
    public void onWheelItemSelected(ConferenceApiModel data) {
        loadBackgroundImage(data.splashImgURL);
        setupConfInfo(data);
        lastSelectedConference = data;
    }

    private void loadBackgroundImage(String url) {
        Glide.with(this)
                .load(url)
                .bitmapTransform(new BlurTransformation(this, 5))
                .crossFade()
                .into(mainImage);
    }

    @Override
    public void onWheelItemClicked(ConferenceApiModel data) {
        setupConfInfo(data);
    }

    private void setupRequiredApis(String cfpUrl, String votingUrl) {
        connection.setupConferenceApi(cfpUrl);
        voteConnection.setupApi(votingUrl);
    }

    private void setupConfInfo(ConferenceApiModel data) {
        currentConferenceLabel.setText(data.country);
        final DateTime startDate = ConferenceManager.parseConfDate(data.fromDate);
        final DateTime endDate = ConferenceManager.parseConfDate(data.toDate);

        final DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
        final String endDateRaw = fmt.print(endDate);

        confInfo.setText(String.format(Locale.getDefault(),
                getString(R.string.selector_conf_info_format),
                startDate.getDayOfMonth(), endDateRaw, data.venue));

        final DateTime now = new DateTime();
        final int days = Days.daysBetween(now, startDate).getDays();
        daysLeft.setupView(getString(R.string.selector_days), days);
        talks.setupView(getString(R.string.selector_talks), Integer.decode(data.sessions));
        capacity.setupView(getString(R.string.selector_capacity), Integer.decode(data.capacity));
    }

    private void setupImageColorFilter() {
        final ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        mainImage.setColorFilter(filter);
    }
}

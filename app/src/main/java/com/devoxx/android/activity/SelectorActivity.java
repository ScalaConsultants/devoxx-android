package com.devoxx.android.activity;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.devoxx.utils.ActivityUtils;
import com.devoxx.utils.BlurTransformation;
import com.devoxx.utils.FontUtils;
import com.devoxx.utils.InfoUtil;
import com.devoxx.utils.Logger;

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


    @Bean
    ConferenceManager conferenceManager;

    @Bean
    Connection connection;

    @Bean
    VoteConnection voteConnection;

    @Bean
    ActivityUtils activityUtils;

    @Bean
    FontUtils fontUtils;

    @Pref
    Settings_ settings;

    @ViewById(R.id.selectorMainContainerImage)
    ImageView mainImage;

    @ViewById(R.id.homeProgressBar)
    ProgressBar progressBar;

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
        conferenceManager.warmUp();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final int statusBarHeight = getStatusBarHeight();
            mainContainer.setPadding(mainContainer.getPaddingLeft(), statusBarHeight,
                    mainContainer.getPaddingRight(), mainContainer.getPaddingBottom());
        }

        fontUtils.applyTypeface(currentConferenceLabel, FontUtils.Font.REGULAR);
        fontUtils.applyTypeface(goButton, FontUtils.Font.REGULAR);
        fontUtils.applyTypeface(confInfo, FontUtils.Font.REGULAR);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        mainImage.setColorFilter(filter);

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

    @Bean
    InfoUtil infoUtil;

    @Click(R.id.selectorRegistrations)
    void onCapacityClick() {
        infoUtil.showToast("Go to registration...");
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
        Glide.with(this)
                .load(R.drawable.devoxx_photo)
                .bitmapTransform(new BlurTransformation(this, 3))
                .crossFade()
                .into(mainImage);


        setupConfInfo(data);
        lastSelectedConference = data;
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

    public int getStatusBarHeight() {
        int result = 0;
        final int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}

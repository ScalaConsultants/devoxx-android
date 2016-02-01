package com.devoxx.android.fragment.talk;

import com.devoxx.android.activity.BaseActivity;
import com.devoxx.android.activity.SpeakerDetailsHostActivity_;
import com.devoxx.android.activity.TalkDetailsHostActivity;
import com.devoxx.android.fragment.schedule.ScheduleLineupFragment;
import com.devoxx.android.view.talk.TalkDetailsSectionClickableItem;
import com.devoxx.android.view.talk.TalkDetailsSectionClickableItem_;
import com.devoxx.android.view.talk.TalkDetailsSectionItem;
import com.devoxx.connection.model.TalkSpeakerApiModel;
import com.devoxx.data.manager.NotificationsManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devoxx.android.fragment.common.BaseFragment;
import com.devoxx.android.view.talk.TalkDetailsHeader;

import com.devoxx.android.view.talk.TalkDetailsSectionItem_;
import com.devoxx.connection.model.SlotApiModel;

import com.devoxx.R;
import com.devoxx.navigation.Navigator;
import com.devoxx.utils.DeviceUtil;

import java.util.List;

@EFragment(R.layout.fragment_talk)
public class TalkFragment extends BaseFragment implements AppBarLayout.OnOffsetChangedListener {

    public static final String DATE_TEXT_FORMAT = "MMMM dd, yyyy"; // April 20, 2014
    public static final String TIME_TEXT_FORMAT = "HH:MM"; // 9:30
    private static final float FULL_FACTOR = 1f;

    @Bean
    DeviceUtil deviceUtil;

    @Bean
    NotificationsManager notificationsManager;

    @Bean
    Navigator navigator;

    @SystemService
    LayoutInflater li;

    @FragmentArg
    SlotApiModel slotApiModel;

    @FragmentArg
    boolean notifyAboutChange;

    @ViewById(R.id.talkDetailsScheduleBtn)
    FloatingActionButton scheduleButton;

    @ViewById(R.id.fragment_talk_toolbar)
    Toolbar toolbar;

    @ViewById(R.id.main_appbar)
    AppBarLayout appBarLayout;

    @ViewById(R.id.main_collapsing)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @ViewById(R.id.toolbar_header_view)
    TalkDetailsHeader toolbarHeaderView;

    @ViewById(R.id.float_header_view)
    TalkDetailsHeader floatHeaderView;

    @ViewById(R.id.talkDetailsContainer)
    LinearLayout sectionContainer;

    @ViewById(R.id.talkDetailsDescription)
    TextView description;

    private boolean shouldHideToolbarHeader = false;
    private SlotApiModel slotModel;

    @AfterViews
    void afterViews() {
        setHasOptionsMenu(!deviceUtil.isLandscapeTablet());
        setupMainLayout();

        if (deviceUtil.isLandscapeTablet() && slotApiModel != null) {
            setupFragment(slotApiModel, notifyAboutChange);
        }
    }

    @Click(R.id.talkDetailsScheduleBtn)
    void onScheduleButtonClick() {
        if (notificationsManager.isNotificationScheduled(slotModel.slotId)) {
            notificationsManager.removeNotification(slotModel.slotId);
        } else {
            notificationsManager.scheduleNotification(slotModel, true);
        }

        if (deviceUtil.isLandscapeTablet()) {
            // Notify ScheduleLineupFragment about change.
            getActivity().sendBroadcast(ScheduleLineupFragment.getRefreshIntent());
        } else {
            notifyHostActivityAboutChangeOccured();
        }

        setupScheduleButton();
    }

    @Click(R.id.talkDetailsNotesBtn)
    void onNotesClick() {
        // TODO
    }

    @Click(R.id.talkDetailsTweetBtn)
    void onTweetClick() {
        // TODO
    }

    @Click(R.id.talkDetailsLikeBtn)
    void onLikeClick() {
        // TODO
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float factor = (float) Math.abs(offset) / (float) maxScroll;

        if (factor == FULL_FACTOR && shouldHideToolbarHeader) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            shouldHideToolbarHeader = !shouldHideToolbarHeader;
        } else if (factor < FULL_FACTOR && !shouldHideToolbarHeader) {
            toolbarHeaderView.setVisibility(View.GONE);
            shouldHideToolbarHeader = !shouldHideToolbarHeader;
        }
    }

    public void setupFragment(SlotApiModel slot, boolean notifyParentAboutChange) {
        slotModel = slot;
        toolbarHeaderView.setupHeader(slot.talk.title, slot.talk.track);
        floatHeaderView.setupHeader(slot.talk.title, slot.talk.track);
        description.setText(Html.fromHtml(slot.talk.summaryAsHtml));
        setupScheduleButton();

        fillSectionsContainer(slot);

        if (notifyParentAboutChange) {
            notifyHostActivityAboutChangeOccured();
        }
    }

    private void notifyHostActivityAboutChangeOccured() {
        final Activity activity = getActivity();
        activity.setResult(TalkDetailsHostActivity.RESULT_CODE_SUCCESS);
    }

    private void setupScheduleButton() {
        if (notificationsManager.isNotificationScheduled(slotModel.slotId)) {
            scheduleButton.setImageResource(R.drawable.ic_star);
        } else {
            scheduleButton.setImageResource(R.drawable.ic_star_border);
        }
    }

    private void fillSectionsContainer(SlotApiModel slotModel) {
        sectionContainer.removeAllViews();
        sectionContainer.addView(createDateTimeSection(slotModel));
        sectionContainer.addView(createPresenterSection(slotModel));
        sectionContainer.addView(createRoomSection(slotModel));
        sectionContainer.addView(createFormatSection(slotModel));
    }

    private View createFormatSection(SlotApiModel slotModel) {
        return createSection(R.drawable.ic_star, R.string.talk_details_section_format, slotModel.talk.talkType);
    }

    private View createRoomSection(SlotApiModel slotModel) {
        return createSection(R.drawable.ic_place_big, R.string.talk_details_section_room, slotModel.roomName);
    }

    private View createPresenterSection(SlotApiModel slotModel) {
        final boolean manyGuys = slotModel.talk.speakers.size() > 1;
        return createClickableSection(R.drawable.ic_microphone_big,
                manyGuys ? R.string.talk_details_section_presentors : R.string.talk_details_section_presentor,
                slotModel.talk.speakers);
    }

    private View createClickableSection(
            @DrawableRes int icon, @StringRes int title, List<TalkSpeakerApiModel> readableSpeakers) {
        final TalkDetailsSectionClickableItem result = TalkDetailsSectionClickableItem_.build(getContext());
        result.setupView(icon, title);
        final ViewGroup container = result.getSpeakersContainer();
        for (TalkSpeakerApiModel speaker : readableSpeakers) {
            final TextView speakerView = (TextView) li.inflate(
                    R.layout.talk_details_section_speaker_item, container, false);
            SpannableString content = new SpannableString(speaker.name);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            speakerView.setText(content);
            final String speakeruuid = TalkSpeakerApiModel.getUuidFromLink(speaker.link);
            speakerView.setOnClickListener(v ->
                    navigator.openSpeakerDetails(getMainActivity(), speakeruuid));
            result.addSpeakerView(speakerView);
        }
        return result;
    }

    private View createDateTimeSection(SlotApiModel slotModel) {
        final DateTime startDate = new DateTime(slotModel.fromTimeMillis);
        final DateTime endDate = new DateTime(slotModel.toTimeMillis);
        final String startDateString = startDate.toString(DateTimeFormat.forPattern(DATE_TEXT_FORMAT));
        final String startTimeString = startDate.toString(DateTimeFormat.forPattern(TIME_TEXT_FORMAT));
        final String endTimeString = endDate.toString(DateTimeFormat.forPattern(TIME_TEXT_FORMAT));
        return createSection(R.drawable.ic_access_time_white_48dp, R.string.talk_details_section_date_time,
                String.format("%s, %s to %s", startDateString, startTimeString, endTimeString));
    }

    private TalkDetailsSectionItem createSection(@DrawableRes int icon, @StringRes int title, String subtitle) {
        final TalkDetailsSectionItem result = TalkDetailsSectionItem_.build(getContext());
        result.setupView(icon, title, subtitle);
        return result;
    }

    private void setupMainLayout() {
        collapsingToolbarLayout.setTitle(" ");
        final BaseActivity baseActivity = ((BaseActivity) getActivity());
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> baseActivity.finish());
            baseActivity.setSupportActionBar(toolbar);
            baseActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        appBarLayout.addOnOffsetChangedListener(this);
    }
}
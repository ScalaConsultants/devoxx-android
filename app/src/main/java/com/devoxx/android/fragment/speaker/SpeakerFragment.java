package com.devoxx.android.fragment.speaker;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Optional;
import com.devoxx.R;
import com.devoxx.android.activity.BaseActivity;
import com.devoxx.android.activity.TalkDetailsHostActivity;
import com.devoxx.android.activity.TalkDetailsHostActivity_;
import com.devoxx.android.fragment.common.BaseFragment;
import com.devoxx.android.view.speaker.SpeakerDetailsHeader;
import com.devoxx.android.view.speaker.SpeakerDetailsTalkItem;
import com.devoxx.android.view.speaker.SpeakerDetailsTalkItem_;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.connection.model.TalkSpeakerApiModel;
import com.devoxx.data.Settings_;
import com.devoxx.data.conference.ConferenceManager;
import com.devoxx.data.manager.AbstractDataManager;
import com.devoxx.data.manager.SlotsDataManager;
import com.devoxx.data.manager.SpeakersDataManager;
import com.devoxx.data.model.RealmSpeaker;
import com.devoxx.data.model.RealmTalk;
import com.devoxx.utils.DeviceUtil;
import com.devoxx.utils.InfoUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.List;

@EFragment(R.layout.fragment_speaker)
public class SpeakerFragment extends BaseFragment implements AppBarLayout.OnOffsetChangedListener {

    private static final float FULL_FACTOR = 1f;

    @FragmentArg
    String speakerUuid;

    @Bean
    SpeakersDataManager speakersDataManager;

    @Bean
    SlotsDataManager slotsDataManager;

    @Bean
    InfoUtil infoUtil;

    @Bean
    ConferenceManager conferenceManager;

    @Bean
    DeviceUtil deviceUtil;

    @Pref
    Settings_ settings;

    @ViewById(R.id.imageSpeaker)
    ImageView imageView;

    @ViewById(R.id.textBio)
    TextView textBio;

    @ViewById(R.id.speakerDetailsFirstButton)
    FloatingActionButton firstButton;

    @ViewById(R.id.speakerDetailsSecondButton)
    FloatingActionButton secondButton;

    @ViewById(R.id.main_toolbar)
    Toolbar toolbar;

    @ViewById(R.id.main_appbar)
    AppBarLayout appBarLayout;

    @ViewById(R.id.main_collapsing)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @ViewById(R.id.toolbar_header_view)
    SpeakerDetailsHeader toolbarHeaderView;

    @ViewById(R.id.float_header_view)
    SpeakerDetailsHeader floatHeaderView;

    @ViewById(R.id.speakerDetailsTalkSection)
    LinearLayout talkSection;

    private RealmSpeaker realmSpeaker;
    private TalkSpeakerApiModel speakerTalkModel;
    private boolean shouldHideToolbarHeader = false;

    @AfterViews
    void afterViews() {
        setHasOptionsMenu(!deviceUtil.isLandscapeTablet());

        setupMainLayout();
        if (speakerUuid != null) {
            setupFragment(speakerUuid);
        }
    }

    @Click(R.id.speakerDetailsFirstButton)
    void onFirstButtonClick() {
        // TODO
    }

    @Click(R.id.speakerDetailsSecondButton)
    void onSecondButtonClick() {
        // TODO
    }

    public void setupFragment(final String uuid) {
        speakersDataManager.fetchSpeakerAsync(conferenceManager.getActiveConferenceId(), uuid,
                new AbstractDataManager.IDataManagerListener<RealmSpeaker>() {
                    @Override
                    public void onDataStartFetching() {

                    }

                    @Override
                    public void onDataAvailable(List<RealmSpeaker> items) {
                        throw new IllegalStateException("Should not be here!");
                    }

                    @Override
                    public void onDataAvailable(RealmSpeaker item) {
                        realmSpeaker = speakersDataManager.getByUuid(uuid);
                        setupView();
                    }

                    @Override
                    public void onDataError() {
                        infoUtil.showToast("Something went wrong...");
                    }
                });
    }

    private void setupView() {
        final String name = determineName();
        final String company = "Company Name"; // TODO Waiting for api company info.
        toolbarHeaderView.setupHeader(name, company);
        floatHeaderView.setupHeader(realmSpeaker.getAvatarURL(), name, company);

        textBio.setText(Html.fromHtml(realmSpeaker.getBioAsHtml().trim()));
        textBio.setMovementMethod(LinkMovementMethod.getInstance());

        if (!realmSpeaker.getAcceptedTalks().isEmpty()) {
            for (final RealmTalk talkModel : realmSpeaker.getAcceptedTalks()) {
                final SpeakerDetailsTalkItem item = SpeakerDetailsTalkItem_.build(getContext());
                item.setupView(talkModel.getTrack(), talkModel.getTitle());
                item.setOnClickListener(v -> {
                    final Optional<SlotApiModel> slotModel = slotsDataManager.
                            getSlotByTalkId(talkModel.getId());
                    if (slotModel.isPresent()) {
                        TalkDetailsHostActivity_.intent(this)
                                .slotApiModel(slotModel.get())
                                .startForResult(TalkDetailsHostActivity.REQUEST_CODE);
                    } else {
                        Toast.makeText(getContext(), "No talk.", Toast.LENGTH_SHORT).show();
                    }
                });
                talkSection.addView(item);
            }
        } else {
            talkSection.setVisibility(View.GONE);
        }
    }

    private String determineName() {
        return speakerTalkModel != null ? (speakerTalkModel.firstName + " " + speakerTalkModel.lastName) :
                realmSpeaker != null ? (realmSpeaker.getFirstName() + " " + realmSpeaker.getLastName()) : null;
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
}

package com.devoxx.android.activity;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.devoxx.data.manager.NotificationsManager;
import com.devoxx.data.manager.SlotsDataManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import com.devoxx.android.fragment.schedule.ScheduleMainFragment_;
import com.devoxx.android.fragment.speaker.SpeakersFragment_;
import com.devoxx.android.fragment.track.TracksMainFragment_;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.data.Settings_;

import com.devoxx.data.manager.SpeakersDataManager;
import com.devoxx.utils.InfoUtil;
import com.devoxx.R;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    private static final String TAG_CONTENT_FRAGMENT = "content_fragment";

    @Bean
    SlotsDataManager slotsDataManager;

    @Bean
    SpeakersDataManager speakersDataManager;

    @Bean
    InfoUtil infoUtil;

    @Pref
    Settings_ settings;

    @ViewById(R.id.toolbarWithSpinner)
    Toolbar toolbar;

    @ViewById(R.id.menuContainer)
    ViewGroup menuContainer;

    @ViewById(R.id.menu_schedule)
    View menuScheduleView;

    @ColorRes(R.color.primary_text)
    int selectedTablColor;

    @ColorRes(R.color.tab_text_unselected)
    int unselectedTablColor;

    private String incomingSlotId;
    private boolean isSavedInstanceState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSavedInstanceState = savedInstanceState != null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra(NotificationsManager.EXTRA_TALK_ID)) {
            incomingSlotId = intent.getStringExtra(
                    NotificationsManager.EXTRA_TALK_ID);
        }
    }

    @AfterViews
    void afterViews() {
        setupToolbar();

        if (!isSavedInstanceState) {
            loadCoreData();
        }
    }

    @Click({R.id.menu_schedule, R.id.menu_tracks, R.id.menu_speakers, R.id.menu_map})
    void onMainMenuClick(View view) {
        setupMenuApperance(view);
        removeFragments();

        switch (view.getId()) {
            case R.id.menu_schedule:
                replaceFragment(ScheduleMainFragment_.builder().build(), false);
                break;
            case R.id.menu_tracks:
                replaceFragment(TracksMainFragment_.builder().build(), false);
                break;
            case R.id.menu_speakers:
                replaceFragment(SpeakersFragment_.builder().build(), false);
                break;
            case R.id.menu_map:
                infoUtil.showToast("TBD");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /** We need to call super to get onActivityResult on calling fragment. */
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupMenuApperance(View clickedMenuItem) {
        final int size = menuContainer.getChildCount();
        for (int i = 0; i < size; i++) {
            final ViewGroup child = (ViewGroup) menuContainer.getChildAt(i);
            final boolean shouldBeSelected = clickedMenuItem.getId() == child.getId();
            child.setSelected(shouldBeSelected);

            final ImageView icon = (ImageView) child.getChildAt(0);
            if (shouldBeSelected) {
                icon.setColorFilter(selectedTablColor);
            } else {
                icon.clearColorFilter();
            }
        }
    }

    private void loadCoreData() {
        if (TextUtils.isEmpty(incomingSlotId)) {
            initIncomingSlotId();
        }

        final boolean fromNotification = !TextUtils.isEmpty(incomingSlotId);
        if (fromNotification) {
            loadDataForNotificationOnColdStart();
        } else {
            onMainMenuClick(menuScheduleView);
        }
    }

    private void initIncomingSlotId() {
        final Intent intent = getIntent();
        if (intent != null && intent.hasExtra(NotificationsManager.EXTRA_TALK_ID)) {
            incomingSlotId = intent.getStringExtra(
                    NotificationsManager.EXTRA_TALK_ID);
        }
    }

    private void loadDataForNotificationOnColdStart() {
        final List<SlotApiModel> items = slotsDataManager.getLastTalks();
        final Optional<SlotApiModel> optModel = Stream.of(items)
                .filter(new SlotApiModel.SameModelPredicate(incomingSlotId))
                .findFirst();

        if (optModel.isPresent()) {
            setupTalkFragment(optModel.get());
        } else {
            // TODO
        }
    }

    private void setupTalkFragment(SlotApiModel slotApiModel) {
        // TODO
//        final FragmentManager fm = getSupportFragmentManager();
//        final Fragment talkFragment = fm.findFragmentByTag(TAG_CONTENT_FRAGMENT);
//        if (talkFragment instanceof TalkFragment) {
//            ((TalkFragment) talkFragment).setupViews(slotApiModel);
//        } else {
//            removeFragments();
//            replaceFragment(TalkFragment_.builder().slotModel(slotApiModel).build());
//        }
    }

    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, false, FragmentTransaction.TRANSIT_NONE);
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(fragment, addToBackStack, FragmentTransaction.TRANSIT_NONE);
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack, int fragmentTransition) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(fragmentTransition);
        ft.replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT);
        ft.attach(fragment);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    private void removeFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            for (Fragment fragment : fragments) {
                if (fragment != null)
                    ft.detach(fragment).remove(fragment);
            }
            ft.commit();
            fragmentManager.executePendingTransactions();
        }
    }

}

package io.scalac.degree.android.activity;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import io.scalac.degree.android.fragment.BaseFragment;
import io.scalac.degree.android.fragment.SpeakersFragment_;
import io.scalac.degree.android.fragment.TalkFragment;
import io.scalac.degree.android.fragment.TalkFragment_;
import io.scalac.degree.android.fragment.TalksFragment_;
import io.scalac.degree.android.fragment.TracksFragment_;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.Settings_;
import io.scalac.degree.data.manager.NotificationsManager;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree.data.manager.SpeakersDataManager;
import io.scalac.degree.utils.InfoUtil;
import io.scalac.degree33.R;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    private static final String TAG_CONTENT_FRAGMENT = "content_fragment";
    public static final String INTENT_FILTER_TALKS_ACTION = "INTENT_FILTER_TALKS_ACTION";
    private static final int UNKNOWN_MENU_RES = -1;

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

    private int toolbarMenuRes = UNKNOWN_MENU_RES;

    private FragmentManager.OnBackStackChangedListener
            onBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            final Fragment currentFragment = getCurrentFragment();
            setupToolbarMenu(currentFragment);
        }
    };

    private void setupToolbarMenu(Fragment currentFragment) {
        boolean needsFilterIcon = false;
        if (currentFragment instanceof BaseFragment) {
            needsFilterIcon = ((BaseFragment) currentFragment).needsFilterToolbarIcon();
        }

        toolbarMenuRes = needsFilterIcon ? R.menu.menu_main_toolbar : UNKNOWN_MENU_RES;

        supportInvalidateOptionsMenu();
    }

    private String incomingSlotId;

    @AfterViews
    protected void afterViews() {
        getSupportFragmentManager().
                addOnBackStackChangedListener(onBackStackChangedListener);

        setupToolbar();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra(NotificationsManager.EXTRA_TALK_ID)) {
            incomingSlotId = intent.getStringExtra(
                    NotificationsManager.EXTRA_TALK_ID);
        }
    }

    @Override
    protected void onDestroy() {
        getSupportFragmentManager().removeOnBackStackChangedListener(onBackStackChangedListener);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadCoreData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (toolbarMenuRes != UNKNOWN_MENU_RES) {
            toolbar.inflateMenu(toolbarMenuRes);

            final MenuItem item = menu.findItem(R.id.action_filter_scheduled);
            if (settings.filterTalksBySchedule().getOr(false)) {
                item.setIcon(R.drawable.ic_visibility_white_24dp);
            } else {
                item.setIcon(R.drawable.ic_visibility_off_white_24dp);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ((item.getItemId() == R.id.action_filter_scheduled)) {
            final boolean currentState = settings.filterTalksBySchedule().getOr(false);
            settings.filterTalksBySchedule().put(!currentState);

            if (!currentState) {
                item.setIcon(R.drawable.ic_visibility_white_24dp);
            } else {
                item.setIcon(R.drawable.ic_visibility_off_white_24dp);
            }

            sendBroadcast(new Intent(INTENT_FILTER_TALKS_ACTION));
        }

        return super.onOptionsItemSelected(item);
    }

    @Click({R.id.menu_schedule, R.id.menu_tracks, R.id.menu_speakers, R.id.menu_map})
    void onMainMenuClick(View view) {
        removeFragments();

        switch (view.getId()) {
            case R.id.menu_schedule:
                replaceFragment(TalksFragment_.builder().build(), false);
                break;
            case R.id.menu_tracks:
                replaceFragment(TracksFragment_.builder().build(), false);
                break;
            case R.id.menu_speakers:
                replaceFragment(SpeakersFragment_.builder().build(), false);
                break;
            case R.id.menu_map:
                infoUtil.showToast("TBD");
                break;
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
            replaceFragment(TalksFragment_.builder().build(), false);
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
        final FragmentManager fm = getSupportFragmentManager();
        final Fragment talkFragment = fm.findFragmentByTag(TAG_CONTENT_FRAGMENT);
        if (talkFragment instanceof TalkFragment) {
            ((TalkFragment) talkFragment).setupViews(slotApiModel);
        } else {
            removeFragments();
            replaceFragment(TalkFragment_.builder().slotModel(slotApiModel).build());
        }
    }

    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(fragment, addToBackStack, FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack, int fragmentTransition) {
        setupToolbarMenu(fragment);

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

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().
                findFragmentByTag(TAG_CONTENT_FRAGMENT);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
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

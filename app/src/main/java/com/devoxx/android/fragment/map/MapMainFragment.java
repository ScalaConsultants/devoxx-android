package com.devoxx.android.fragment.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.devoxx.R;
import com.devoxx.android.activity.AboutActivity_;
import com.devoxx.android.activity.SettingsActivity_;
import com.devoxx.android.adapter.map.MapPagerAdapter;
import com.devoxx.android.view.NonSwipeableViewPager;
import com.devoxx.data.conference.ConferenceManager;
import com.devoxx.data.model.RealmConference;
import com.devoxx.utils.InfoUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import java.util.Arrays;
import java.util.List;

@EFragment(R.layout.fragment_maps)
public class MapMainFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private static final int CHECK_PERMISSION_REQ_CODE = 99;
    private static final String[] MAP_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    @Bean
    InfoUtil infoUtil;

    @Bean
    ConferenceManager conferenceManager;

    @ViewById(R.id.tab_layout)
    TabLayout tabLayout;

    @ViewById(R.id.pager)
    NonSwipeableViewPager pager;

    @ViewById(R.id.mapConfTitle)
    TextView confTitle;

    @ViewById(R.id.mapConfSubtitle)
    TextView confSubitle;

    @ColorRes(R.color.primary_text)
    int selectedTablColor;

    @ColorRes(R.color.tab_text_unselected)
    int unselectedTablColor;

    @ColorRes(R.color.primary_text)
    int tabStripColor;

    @AfterViews
    @SuppressLint("DefaultLocale")
    protected void afterViews() {
        final RealmConference conference = conferenceManager.getActiveConference();
        confTitle.setText(conference.getVenue());
        confSubitle.setText(conference.getAddress());

        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != CHECK_PERMISSION_REQ_CODE) {
            return;
        }

        final boolean isGranted = isGranted(grantResults);
        setupPager(isGranted);
        if (!isGranted) {
            infoUtil.showToast(R.string.map_permissions_failure);
        }
    }

    @OptionsItem(R.id.action_settings)
    void onSettingsClick() {
        SettingsActivity_.intent(this).start();
    }

    @OptionsItem(R.id.action_about)
    void onAboutClick() {
        AboutActivity_.intent(this).start();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        // Nothing.
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        // Nothing.
    }

    private void checkPermissions() {
        if (checkGivenPermissions(getActivity(), MAP_PERMISSIONS)) {
            setupPager(true);
        } else {
            requestPermissions(MAP_PERMISSIONS, CHECK_PERMISSION_REQ_CODE);
        }
    }

    private List<Integer> getFloors() {
        return Arrays.asList(1, 2);
    }

    private void setupPager(boolean withMap) {
        final MapPagerAdapter adapter = new MapPagerAdapter(getChildFragmentManager(),
                getFloors(), withMap);
        tabLayout.setTabTextColors(unselectedTablColor, selectedTablColor);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setSelectedTabIndicatorColor(tabStripColor);

        if (withMap) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.venue));
        }

        // TODO Floor will be added from configuration.
        for (Integer floor : getFloors()) {
            tabLayout.addTab(tabLayout.newTab().setText(String.format("%d FLOOR", floor)));
        }
        tabLayout.setOnTabSelectedListener(this);

        pager.setAdapter(adapter);
    }

    private boolean isGranted(int[] grantResults) {
        boolean result = true;
        for (int granResult : grantResults) {
            result &= granResult == PackageManager.PERMISSION_GRANTED;
        }
        return result;
    }

    private static boolean checkGivenPermissions(Activity activity, String... permissions) {
        boolean result = true;
        for (String permission : permissions) {
            result &= ActivityCompat.checkSelfPermission(activity, permission)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return result;
    }
}

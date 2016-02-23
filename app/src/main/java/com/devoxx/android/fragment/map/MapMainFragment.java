package com.devoxx.android.fragment.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.devoxx.R;
import com.devoxx.android.adapter.map.MapPagerAdapter;
import com.devoxx.android.fragment.common.BaseMenuFragment;
import com.devoxx.android.view.NonSwipeableViewPager;
import com.devoxx.data.conference.ConferenceManager;
import com.devoxx.data.model.RealmConference;
import com.devoxx.data.model.RealmFloor;
import com.devoxx.utils.DeviceUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import java.util.List;

@EFragment(R.layout.fragment_maps)
public class MapMainFragment extends BaseMenuFragment
        implements TabLayout.OnTabSelectedListener {

    public static final int CHECK_PERMISSION_REQ_CODE = 99;
    public static final String[] MAP_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    @Bean
    DeviceUtil deviceUtil;

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
    protected void afterViewsInner() {
        setHasOptionsMenu(true);
        final Optional<RealmConference> conference = conferenceManager.getActiveConference();
        if (conference.isPresent()) {
            confTitle.setText(conference.get().getVenue());
            confSubitle.setText(conference.get().getAddress());
        }

        checkPermissions();
    }

    @Override
    protected int getMenuRes() {
        return R.menu.map_menu;
    }

    @Override
    protected void onSearchQuery(String query) {
        // Not needed.
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

    private void setupPager(boolean withMap) {
        final Optional<RealmConference> conference = conferenceManager.getActiveConference();
        if (conference.isPresent()) {
            final String res = deviceUtil.isTablet() ? "tablet" : "phone";
            final List<RealmFloor> floors = RealmConference.extractFloors(conference.get(), res);
            final int floorsCount = floors.size();

            final List<String> floorsImages = Stream.of(floors)
                    .map(RealmFloor::getImg).collect(Collectors.toList());

            final MapPagerAdapter adapter = new MapPagerAdapter(
                    getChildFragmentManager(), floorsCount, withMap, floorsImages);
            tabLayout.removeAllTabs();
            tabLayout.setTabTextColors(unselectedTablColor, selectedTablColor);
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setSelectedTabIndicatorColor(tabStripColor);

            if (withMap) {
                tabLayout.addTab(tabLayout.newTab().setText(R.string.venue));
            }

            for (int i = 0; i < floorsCount; i++) {
                final RealmFloor floor = floors.get(i);
                tabLayout.addTab(tabLayout.newTab().setText(floor.getTitle()));
            }

            tabLayout.setOnTabSelectedListener(this);

            pager.setAdapter(adapter);
        }
    }

    public static boolean isGranted(int[] grantResults) {
        boolean result = true;
        for (int granResult : grantResults) {
            result &= granResult == PackageManager.PERMISSION_GRANTED;
        }
        return result;
    }

    public static boolean checkGivenPermissions(Activity activity, String... permissions) {
        boolean result = true;
        for (String permission : permissions) {
            result &= ActivityCompat.checkSelfPermission(activity, permission)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return result;
    }
}

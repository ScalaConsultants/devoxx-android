package com.devoxx.android.fragment.map;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devoxx.R;
import com.devoxx.android.fragment.common.BaseMenuFragment;
import com.devoxx.data.conference.ConferenceManager;
import com.devoxx.data.model.RealmConference;
import com.devoxx.data.model.RealmFloor;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EFragment(R.layout.fragment_maps_landscape)
public class MapMenuLandscapeFragment extends BaseMenuFragment {

    @Bean
    ConferenceManager conferenceManager;

    @SystemService
    LayoutInflater layoutInflater;

    @ViewById(R.id.mapsMenuFragmentContainer)
    LinearLayout menuContaier;

    @Override
    protected int getMenuRes() {
        return R.menu.map_menu;
    }

    @AfterViews
    @SuppressLint("DefaultLocale")
    protected void afterViews() {
        setHasOptionsMenu(true);
        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != MapMainFragment.CHECK_PERMISSION_REQ_CODE) {
            return;
        }

        final boolean isGranted = MapMainFragment.isGranted(grantResults);
        setupMenu(isGranted);
        if (!isGranted) {
            infoUtil.showToast(R.string.map_permissions_failure);
        }
    }

    private void checkPermissions() {
        if (MapMainFragment.checkGivenPermissions(getActivity(), MapMainFragment.MAP_PERMISSIONS)) {
            setupMenu(true);
        } else {
            requestPermissions(MapMainFragment.MAP_PERMISSIONS, MapMainFragment.CHECK_PERMISSION_REQ_CODE);
        }
    }

    private void setupMenu(boolean withMap) {
        menuContaier.removeAllViews();
        final RealmConference conference = conferenceManager.getActiveConference();
        final List<RealmFloor> floors = RealmConference.extractFloors(conference);
        final int floorsCount = floors.size();

        boolean mapOpened = false;
        if (withMap) {
            final View mapMenuItem = createMenuItem(getString(R.string.venue), this::openMap);
            menuContaier.addView(mapMenuItem);
            mapMenuItem.performClick();
            mapOpened = true;
        }

        View floorMenuItem = null;
        for (int i = 0; i < floorsCount; i++) {
            final RealmFloor floor = floors.get(i);
            floorMenuItem = createMenuItem(floor.getTitle(), () -> openFloor(floor.getImg()));
            menuContaier.addView(floorMenuItem);
        }

        if (!mapOpened && floorMenuItem != null) {
            floorMenuItem.performClick();
        }
    }

    private void openFloor(String floorImageUrl) {
        final MapFloorFragment fr = MapFloorFragment_.builder().imageUrl(floorImageUrl).build();
        getMainActivity().replaceFragmentInGivenContainer(fr, false, R.id.content_frame_second);
    }

    private View createMenuItem(String title, Runnable action) {
        final View result = layoutInflater.inflate(R.layout.map_landscape_menu_item, menuContaier, false);
        final TextView label = (TextView) result.findViewById(R.id.mapLandscapeMenuTitle);
        label.setText(title);
        result.setOnClickListener(v -> {
            final int size = menuContaier.getChildCount();
            for (int i = 0; i < size; i++) {
                final View child = menuContaier.getChildAt(i);
                child.setSelected(child.equals(v));
            }
            action.run();
        });
        return result;
    }

    private void openMap() {
        final MapGoogleFragment fr = MapGoogleFragment_.builder().build();
        getMainActivity().replaceFragmentInGivenContainer(fr, false, R.id.content_frame_second);
    }

    @Override
    protected void onSearchQuery(String query) {
        // Not needed.
    }
}

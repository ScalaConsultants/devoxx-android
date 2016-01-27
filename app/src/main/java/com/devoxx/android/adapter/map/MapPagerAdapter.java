package com.devoxx.android.adapter.map;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.devoxx.android.fragment.map.MapFloorFragment_;
import com.devoxx.android.fragment.map.MapGoogleFragment_;

import java.util.List;

public class MapPagerAdapter extends FragmentStatePagerAdapter {

    private static final int MAP_GOOGLE_POSITION = 0;
    private final List<Integer> floors;

    public MapPagerAdapter(FragmentManager fm, List<Integer> floorsList) {
        super(fm);
        floors = floorsList;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case MAP_GOOGLE_POSITION:
                return MapGoogleFragment_.builder().build();
            default:
                return MapFloorFragment_.builder().floor(position).build();
        }
    }

    @Override
    public int getCount() {
        return floors.size() + 1 /* +1 for Google Map */;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}

package com.devoxx.android.adapter.map;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.devoxx.android.fragment.map.MapFloorFragment_;
import com.devoxx.android.fragment.map.MapGoogleFragment_;

public class MapPagerAdapter extends FragmentStatePagerAdapter {

    private static final int MAP_GOOGLE_POSITION = 0;
    private final int floorsCount;
    private final boolean withMap;

    public MapPagerAdapter(FragmentManager fm, int floorsVal, boolean map) {
        super(fm);
        floorsCount = floorsVal;
        withMap = map;
    }

    @Override
    public Fragment getItem(int position) {
        if (withMap && position == MAP_GOOGLE_POSITION) {
            return MapGoogleFragment_.builder().build();
        } else {
            return MapFloorFragment_.builder().floor(position).build();
        }
    }

    @Override
    public int getCount() {
        return floorsCount + (withMap ? 1 : 0) /* +1 for Google Map */;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}

package com.devoxx.android.adapter.schedule;

import com.devoxx.android.fragment.schedule.ScheduleLineupFragment;
import com.devoxx.data.conference.model.ConferenceDay;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.devoxx.android.fragment.schedule.ScheduleLineupFragment_;

public class SchedulePagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAB_LAYOUT_TEXT_FORMAT = "EEE dd"; // WED 04
    private static final SimpleDateFormat TAB_LAYOUT_FORMAT = new SimpleDateFormat(
            TAB_LAYOUT_TEXT_FORMAT, Locale.getDefault());

    private static final Date TMP_DATE = new Date();

    private final List<ConferenceDay> conferenceDays;
    private final HashMap<Integer, ScheduleLineupFragment> fragmentHashMap;

    public SchedulePagerAdapter(FragmentManager fm, List<ConferenceDay> slots) {
        super(fm);
        conferenceDays = new ArrayList<>(slots);
        fragmentHashMap = new HashMap<>();
    }

    public ScheduleLineupFragment getFragment(int position) {
        return fragmentHashMap.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        final ScheduleLineupFragment result = ScheduleLineupFragment_.builder()
                .lineupDayMs(conferenceDays.get(position).getDayMs())
                .build();
        fragmentHashMap.put(position, result);
        return result;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        fragmentHashMap.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return conferenceDays.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        TMP_DATE.setTime(conferenceDays.get(position).getDayMs());
        return TAB_LAYOUT_FORMAT.format(TMP_DATE);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}

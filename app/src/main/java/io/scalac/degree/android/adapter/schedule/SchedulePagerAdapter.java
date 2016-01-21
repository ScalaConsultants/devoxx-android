package io.scalac.degree.android.adapter.schedule;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.scalac.degree.android.fragment.schedule.ScheduleDayLinupFragment_;
import io.scalac.degree.utils.Logger;

public class SchedulePagerAdapter extends FragmentPagerAdapter {

    private static final String TAB_LAYOUT_TEXT_FORMAT = "EEE dd"; // WED 04
    private static final SimpleDateFormat TAB_LAYOUT_FORMAT = new SimpleDateFormat(
            TAB_LAYOUT_TEXT_FORMAT, Locale.getDefault());

    private static final Date TMP_DATE = new Date();

    private final List<Long> scheduleDays;

    public SchedulePagerAdapter(FragmentManager fm, List<Long> slots) {
        super(fm);

        scheduleDays = new ArrayList<>(slots);
    }

    @Override
    public Fragment getItem(int position) {
        return ScheduleDayLinupFragment_.builder()
                .lineupDayMs(scheduleDays.get(position)).build();
    }

    @Override
    public int getCount() {
        return scheduleDays.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        TMP_DATE.setTime(scheduleDays.get(position));
        final CharSequence result = TAB_LAYOUT_FORMAT.format(TMP_DATE);
        Logger.l("Schedule label: " + result.toString());
        return result;
    }

    public void refreshDays(List<Long> days) {
        scheduleDays.clear();
        scheduleDays.addAll(days);
    }
}

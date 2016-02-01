package com.devoxx.android.fragment.schedule;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.devoxx.R;
import com.devoxx.android.activity.TalkDetailsHostActivity;
import com.devoxx.android.adapter.schedule.SchedulePagerAdapter;
import com.devoxx.android.dialog.FiltersDialog;
import com.devoxx.android.fragment.common.BaseMenuFragment;
import com.devoxx.data.conference.ConferenceManager;
import com.devoxx.data.conference.model.ConferenceDay;
import com.devoxx.data.manager.SlotsDataManager;
import com.devoxx.data.schedule.filter.model.RealmScheduleDayItemFilter;
import com.devoxx.data.schedule.search.ScheduleLineupSearchManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_schedules)
public class ScheduleMainFragment extends BaseMenuFragment
        implements FiltersDialog.IFiltersChangedListener {

    @Bean
    SlotsDataManager slotsDataManager;

    @Bean
    ScheduleLineupSearchManager scheduleLineupSearchManager;

    @Bean
    ConferenceManager conferenceManager;

    @ViewById(R.id.tab_layout)
    TabLayout tabLayout;

    @ViewById(R.id.pager)
    ViewPager viewPager;

    @ColorRes(R.color.primary_text)
    int selectedTablColor;

    @ColorRes(R.color.tab_text_unselected)
    int unselectedTablColor;

    @ColorRes(R.color.primary_text)
    int tabStripColor;

    @AfterViews
    void afterViewsInner() {
        super.afterViews();
        invalidateViewPager();

        tabLayout.setTabTextColors(unselectedTablColor, selectedTablColor);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setSelectedTabIndicatorColor(tabStripColor);
    }

    @Override
    public int getMenuRes() {
        return R.menu.schedule_menu;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TalkDetailsHostActivity.REQUEST_CODE
                && resultCode == TalkDetailsHostActivity.RESULT_CODE_SUCCESS) {
            notifyRestScheduleLineupFragments(requestCode, resultCode, data);
        }
    }

    private void notifyRestScheduleLineupFragments(int requestCode, int resultCode, Intent data) {
        final List<Fragment> fragments = getChildFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof ScheduleLineupFragment) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onDestroy() {
        scheduleLineupSearchManager.clearLastQuery();
        super.onDestroy();
    }

    @Override
    public void onFiltersCleared() {
        super.onFiltersCleared();
        invalidateViewPager();
    }

    @Override
    public void onFiltersDismissed() {
        super.onFiltersDismissed();
        invalidateViewPager();
    }

    @Override
    public void onFiltersDefault() {
        super.onFiltersDefault();
        invalidateViewPager();
    }

    private void invalidateViewPager() {
        final List<ConferenceDay> days = combineDaysWithFilters();
        final SchedulePagerAdapter schedulePagerAdapter
                = new SchedulePagerAdapter(getChildFragmentManager(), days);

        viewPager.setAdapter(schedulePagerAdapter);
        schedulePagerAdapter.notifyDataSetChanged();

        tabLayout.setupWithViewPager(viewPager);

        final ConferenceDay activeDay = conferenceManager.getCurrentConfDay();
        if (days.contains(activeDay)) {
            final int index = days.indexOf(activeDay);
            viewPager.setCurrentItem(index);
        }
    }

    @Override
    protected void onSearchQuery(String query) {
        scheduleLineupSearchManager.saveLastQuery(query);
        getMainActivity().sendBroadcast(new Intent(
                ScheduleLineupSearchManager.SEARCH_INTENT_ACTION));
    }

    private List<ConferenceDay> combineDaysWithFilters() {
        final List<RealmScheduleDayItemFilter> filters =
                scheduleFilterManager.getActiveDayFilters();
        final List<ConferenceDay> days = conferenceManager.getConferenceDays();
        final List<ConferenceDay> result = new ArrayList<>();
        for (ConferenceDay day : days) {
            for (RealmScheduleDayItemFilter filter : filters) {
                if (filter.getLabel().equalsIgnoreCase(day.getName())) {
                    result.add(day);
                }
            }
        }
        return result;
    }
}

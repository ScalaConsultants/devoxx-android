package com.devoxx.android.fragment.schedule;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.annimon.stream.Optional;
import com.devoxx.R;
import com.devoxx.android.activity.TalkDetailsHostActivity;
import com.devoxx.android.adapter.schedule.SchedulePagerAdapter;
import com.devoxx.android.dialog.FiltersDialog;
import com.devoxx.android.fragment.common.BaseMenuFragment;
import com.devoxx.data.conference.model.ConferenceDay;
import com.devoxx.data.manager.SlotsDataManager;
import com.devoxx.data.schedule.filter.model.RealmScheduleDayItemFilter;
import com.devoxx.data.schedule.search.SearchManager;
import com.devoxx.navigation.NeededUpdateListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_schedules)
public class ScheduleMainFragment extends BaseMenuFragment
        implements FiltersDialog.IFiltersChangedListener, ViewPager.OnPageChangeListener {

    @Bean
    SlotsDataManager slotsDataManager;

    @Bean
    SearchManager searchManager;

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

    private SchedulePagerAdapter schedulePagerAdapter;

    @AfterViews
    void afterViewsInner() {
        super.afterViews();
        invalidateViewPager();

        tabLayout.setTabTextColors(unselectedTablColor, selectedTablColor);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setSelectedTabIndicatorColor(tabStripColor);

        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public int getMenuRes() {
        return R.menu.schedule_menu;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (navigator.isUpdateNeeded()) {
            notifyRestScheduleLineupFragments();
        }
    }

    private void notifyRestScheduleLineupFragments() {
        final List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof ScheduleLineupFragment) {
                    ((NeededUpdateListener) fragment).refreshData();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        viewPager.removeOnPageChangeListener(this);
        searchManager.clearLastQuery();
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
        schedulePagerAdapter = new SchedulePagerAdapter(getChildFragmentManager(), days);

        viewPager.setAdapter(schedulePagerAdapter);
        schedulePagerAdapter.notifyDataSetChanged();

        tabLayout.setupWithViewPager(viewPager);

        final Optional<ConferenceDay> opt = conferenceManager.getCurrentConfDay();
        if (opt.isPresent() && days.contains(opt.get())) {
            final int index = days.indexOf(opt.get());
            viewPager.setCurrentItem(index);
        }
    }

    @Override
    protected void onSearchQuery(String query) {
        searchManager.saveLastQuery(query);
        getMainActivity().sendBroadcast(new Intent(
                SearchManager.SEARCH_INTENT_ACTION));
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Nothing.
    }

    @Override
    public void onPageSelected(int position) {
        final ScheduleLineupFragment fragment = schedulePagerAdapter.getFragment(position);
        if (fragment != null) {
            fragment.triggerRunningSessionCheck();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // Nothing.
    }
}

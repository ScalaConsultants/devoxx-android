package com.devoxx.android.fragment.track;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.devoxx.R;
import com.devoxx.android.activity.TalkDetailsHostActivity;
import com.devoxx.android.adapter.track.TracksPagerAdapter;
import com.devoxx.android.dialog.FiltersDialog;
import com.devoxx.android.fragment.common.BaseMenuFragment;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.data.manager.SlotsDataManager;
import com.devoxx.data.schedule.filter.model.RealmScheduleTrackItemFilter;
import com.devoxx.data.schedule.search.SearchManager;
import com.devoxx.navigation.NeededUpdateListener;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import java.util.List;

@EFragment(R.layout.fragment_tracks)
public class TracksMainFragment extends BaseMenuFragment
        implements FiltersDialog.IFiltersChangedListener {

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

    private TracksPagerAdapter tracksPagerAdapter;

    @AfterInject
    void afterInject() {
        final List<SlotApiModel> slotApiModelList = slotsDataManager.getLastTalks();
        final List<SlotApiModel> resultList = filterByTrack(slotApiModelList);
        tracksPagerAdapter = new TracksPagerAdapter(getChildFragmentManager(), resultList);
    }

    @AfterViews
    void afterViewsInternal() {
        super.afterViews();

        viewPager.setAdapter(tracksPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(unselectedTablColor, selectedTablColor);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setSelectedTabIndicatorColor(tabStripColor);
    }

    @Override
    protected int getMenuRes() {
        return R.menu.tracks_menu;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (navigator.isUpdateNeeded()) {
            notifyRestScheduleLineupFragments();
        }
    }

    @Override
    public void onDestroy() {
        searchManager.clearLastQuery();
        super.onDestroy();
    }

    @Override
    protected void onSearchQuery(String query) {
        searchManager.saveLastQuery(query);

        final List<SlotApiModel> resultList = filterByTrack(doQuery(query));
        tracksPagerAdapter.setData(resultList);
        viewPager.setAdapter(tracksPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tracksPagerAdapter.notifyDataSetChanged();

    }

    @Override
    public void onFiltersCleared() {
        super.onFiltersCleared();
        invalidateAdapterOnFiltersChange();
    }

    @Override
    public void onFiltersDismissed() {
        super.onFiltersDismissed();
        invalidateAdapterOnFiltersChange();
    }

    @Override
    public void onFiltersDefault() {
        super.onFiltersDefault();
        invalidateAdapterOnFiltersChange();
    }

    private List<SlotApiModel> doQuery(String query) {
        final List<SlotApiModel> slotApiModelList = slotsDataManager.getLastTalks();
        return Stream.of(slotApiModelList)
                .filter(new SlotApiModel.FilterPredicate(query))
                .collect(Collectors.toList());
    }

    private void invalidateAdapterOnFiltersChange() {
        final List<SlotApiModel> slotApiModelList = slotsDataManager.getLastTalks();
        final List<SlotApiModel> resultList = filterByTrack(slotApiModelList);
        tracksPagerAdapter = new TracksPagerAdapter(getChildFragmentManager(), resultList);
        viewPager.setAdapter(tracksPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tracksPagerAdapter.notifyDataSetChanged();
    }

    private List<SlotApiModel> filterByTrack(List<SlotApiModel> resultList) {
        final List<RealmScheduleTrackItemFilter> trackFilters
                = scheduleFilterManager.getActiveTrackFilters();
        return Stream.of(resultList)
                .filter(value -> {
                    for (RealmScheduleTrackItemFilter trackFilter : trackFilters) {
                        final String trackName = trackFilter.getTrackName();
                        final String trackId = trackFilter.getTrackId();
                        final String slotTrack = value.talk.track.toLowerCase();
                        final String slotTrackId = value.talk.trackId.toLowerCase();
                        if (value.isTalk() && (slotTrack.equalsIgnoreCase(trackId))
                                || slotTrack.equalsIgnoreCase(trackName)
                                || slotTrackId.equalsIgnoreCase(trackId)
                                || slotTrackId.equalsIgnoreCase(trackName)) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    private void notifyRestScheduleLineupFragments() {
        final List<Fragment> fragments = getChildFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof TracksListFragment) {
                ((NeededUpdateListener) fragment).refreshData();
            }
        }
    }
}

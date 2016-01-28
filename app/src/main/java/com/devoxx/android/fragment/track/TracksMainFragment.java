package com.devoxx.android.fragment.track;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.devoxx.android.activity.AboutActivity_;
import com.devoxx.android.activity.SettingsActivity_;
import com.devoxx.android.activity.TalkDetailsHostActivity;
import com.devoxx.android.adapter.track.TracksPagerAdapter;
import com.devoxx.android.dialog.FiltersDialog;
import com.devoxx.android.fragment.schedule.ScheduleDayLinupFragment;
import com.devoxx.data.schedule.filter.ScheduleFilterManager;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import android.app.SearchManager;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import com.devoxx.android.fragment.common.BaseFragment;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.data.manager.SlotsDataManager;
import com.devoxx.data.schedule.filter.model.RealmScheduleDayItemFilter;
import com.devoxx.data.schedule.filter.model.RealmScheduleTrackItemFilter;
import com.devoxx.R;

@EFragment(R.layout.fragment_tracks)
public class TracksMainFragment extends BaseFragment implements FiltersDialog.IFiltersChangedListener {

    @Bean
    SlotsDataManager slotsDataManager;

    @Bean
    ScheduleFilterManager scheduleFilterManager;

    @SystemService
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
    void afterViews() {
        setHasOptionsMenu(true);

        viewPager.setAdapter(tracksPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(unselectedTablColor, selectedTablColor);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setSelectedTabIndicatorColor(tabStripColor);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tracks_menu, menu);
        setupFilterMenu(menu);
        setupSearchView(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @OptionsItem(R.id.action_filter)
    void onFilterClicked() {
        final List<RealmScheduleDayItemFilter> dayFilters = scheduleFilterManager.getDayFilters();
        final List<RealmScheduleTrackItemFilter> trackFilters = scheduleFilterManager.getTrackFilters();
        FiltersDialog.showFiltersDialog(getContext(), dayFilters, trackFilters, this);
    }

    @OptionsItem(R.id.action_about)
    void onAboutClick() {
        AboutActivity_.intent(this).start();
    }

    @OptionsItem(R.id.action_settings)
    void onSettingsClick() {
        SettingsActivity_.intent(this).start();
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
            if (fragment instanceof TracksListFragment) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void setupFilterMenu(Menu menu) {
        if (scheduleFilterManager.isSomeFiltersActive()) {
            menu.findItem(R.id.action_filter).setIcon(R.drawable.ic_filter_white_24px);
        } else {
            menu.findItem(R.id.action_filter).setIcon(R.drawable.ic_filter_outline_white_24px);
        }
    }

    private void setupSearchView(Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    onSearchQuery(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    onSearchQuery(s);
                    return false;
                }
            });

            searchView.setOnCloseListener(() -> {
                onSearchQuery("");
                return false;
            });

            searchView.setQueryHint(getString(R.string.search_hint));
        }
    }

    private void onSearchQuery(String query) {
        final List<SlotApiModel> resultList = filterByTrack(doQuery(query));
        tracksPagerAdapter.setData(resultList);
        viewPager.setAdapter(tracksPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tracksPagerAdapter.notifyDataSetChanged();

    }

    private List<SlotApiModel> doQuery(String query) {
        final List<SlotApiModel> slotApiModelList = slotsDataManager.getLastTalks();
        return Stream.of(slotApiModelList)
                .filter(createFilter(query))
                .collect(Collectors.toList());
    }

    private Predicate<? super SlotApiModel> createFilter(String query) {
        return value -> value.isTalk() && (value.talk.track.toLowerCase().contains(query)
                || value.talk.title.toLowerCase().contains(query)
                || value.talk.getReadableSpeakers().contains(query)
                || value.talk.summary.contains(query));
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
                        if (value.isTalk() && (value.talk.track
                                .equalsIgnoreCase(trackFilter.getTrackName()))
                                || value.talk.track
                                .equalsIgnoreCase(trackFilter.getLabel())) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void onDayFiltersChanged(RealmScheduleDayItemFilter itemFilter, boolean isActive) {
        scheduleFilterManager.updateFilter(itemFilter, isActive);
    }

    @Override
    public void onTrackFiltersChanged(RealmScheduleTrackItemFilter itemFilter, boolean isActive) {
        scheduleFilterManager.updateFilter(itemFilter, isActive);
    }

    @Override
    public void onFiltersCleared() {
        scheduleFilterManager.clearFilters();
        invalidateAdapterOnFiltersChange();
    }

    @Override
    public void onFiltersDismissed() {
        invalidateAdapterOnFiltersChange();
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public void onFiltersDefault() {
        scheduleFilterManager.defaultFilters();
        invalidateAdapterOnFiltersChange();
    }
}

package io.scalac.degree.android.fragment.schedule;

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
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import io.scalac.degree.android.activity.TalkDetailsHostActivity;
import io.scalac.degree.android.adapter.schedule.SchedulePagerAdapter;
import io.scalac.degree.android.dialog.FiltersDialog;
import io.scalac.degree.android.fragment.common.BaseFragment;
import io.scalac.degree.data.conference.ConferenceManager;
import io.scalac.degree.data.conference.model.ConferenceDay;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree.data.schedule.filter.ScheduleFilterManager;
import io.scalac.degree.data.schedule.filter.model.RealmScheduleDayItemFilter;
import io.scalac.degree.data.schedule.filter.model.RealmScheduleTrackItemFilter;
import io.scalac.degree.data.schedule.search.ScheduleLineupSearchManager;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_schedules)
public class ScheduleMainFragment extends BaseFragment
        implements FiltersDialog.IFiltersChangedListener {

    @SystemService
    SearchManager searchManager;

    @Bean
    SlotsDataManager slotsDataManager;

    @Bean
    ScheduleLineupSearchManager scheduleLineupSearchManager;

    @Bean
    ConferenceManager conferenceManager;

    @Bean
    ScheduleFilterManager scheduleFilterManager;

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
    void afterViews() {
        invalidateViewPager();

        tabLayout.setTabTextColors(unselectedTablColor, selectedTablColor);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setSelectedTabIndicatorColor(tabStripColor);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.speakers_menu, menu);

        setupSearchView(menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @OptionsItem(R.id.action_filter)
    void onFilterClicked() {
        final List<RealmScheduleDayItemFilter> dayFilters = scheduleFilterManager.getDayFilters();
        final List<RealmScheduleTrackItemFilter> trackFilters = scheduleFilterManager.getTrackFilters();
        FiltersDialog.showFiltersDialog(getContext(), dayFilters, trackFilters, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TalkDetailsHostActivity.REQUEST_CODE
                && resultCode == TalkDetailsHostActivity.RESULT_CODE_SUCCESS) {
            invalidateViewPager();
        }
    }

    private void setupSearchView(Menu menu) {
        // TODO Get conference days from ConferenceManager class to build filter menu.

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

    @Override
    public void onDestroy() {
        scheduleLineupSearchManager.clearLastQuery();
        super.onDestroy();
    }

    @Override
    public void onDayFiltersChanged(RealmScheduleDayItemFilter itemFilter, boolean isActive) {
        // TODO update UI
        scheduleFilterManager.updateFilter(itemFilter, isActive);
    }

    @Override
    public void onTrackFiltersChanged(RealmScheduleTrackItemFilter itemFilter, boolean isActive) {
        // TODO update UI
        scheduleFilterManager.updateFilter(itemFilter, isActive);
    }

    @Override
    public void onFiltersCleared() {
        scheduleFilterManager.clearFilters();
        invalidateViewPager();
    }

    @Override
    public void onFiltersDismissed() {
        invalidateViewPager();

        getMainActivity().sendBroadcast(new Intent(
                ScheduleFilterManager.FILTERS_CHANGED_ACTION));
    }

    @Override
    public void onFiltersDefault() {
        scheduleFilterManager.defaultFilters();
        invalidateViewPager();
    }

    private void invalidateViewPager() {
        final List<ConferenceDay> days = combineDaysWithFilters();
        final SchedulePagerAdapter schedulePagerAdapter
                = new SchedulePagerAdapter(getChildFragmentManager(), days);

        viewPager.setAdapter(schedulePagerAdapter);
        schedulePagerAdapter.notifyDataSetChanged();

        tabLayout.setupWithViewPager(viewPager);
    }

    private void onSearchQuery(String query) {
        // TODO Adds labels with filters! x_label
        scheduleLineupSearchManager.saveLastQuery(query);
        getMainActivity().sendBroadcast(new Intent(
                ScheduleLineupSearchManager.SEARCH_INTENT_ACTION));
    }

    private List<ConferenceDay> combineDaysWithFilters() {
        final List<RealmScheduleDayItemFilter> filters = scheduleFilterManager.getActiveDayFilters();
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

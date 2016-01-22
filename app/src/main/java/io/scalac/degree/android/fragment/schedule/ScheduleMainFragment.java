package io.scalac.degree.android.fragment.schedule;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
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
import android.view.SubMenu;

import java.util.List;

import io.scalac.degree.android.adapter.schedule.SchedulePagerAdapter;
import io.scalac.degree.android.adapter.schedule.model.creator.ScheduleLineupSearchManager;
import io.scalac.degree.android.fragment.common.BaseFragment;
import io.scalac.degree.data.conference.ConferenceManager;
import io.scalac.degree.data.conference.model.ConferenceDay;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_schedules)
public class ScheduleMainFragment extends BaseFragment {

    @SystemService
    SearchManager searchManager;

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

    private SchedulePagerAdapter schedulePagerAdapter;

    @AfterInject
    void afterInject() {
        final List<ConferenceDay> days = conferenceManager.getConferenceDays();
        schedulePagerAdapter = new SchedulePagerAdapter(getChildFragmentManager(), days);
    }

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

        setupFilters(menu);
        setupSearchView(menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!item.isCheckable()) {
            return false;
        }

        // The id of the menu item is just hashCode() on the conference day name.
        final int itemID = item.getItemId();
        final boolean isChecked = item.isChecked();
        if (isChecked) {
            schedulePagerAdapter.removePage(itemID);
        } else {
            schedulePagerAdapter.addPage(itemID);
        }
        item.setChecked(!isChecked);

        invalidateViewPager();

        return super.onOptionsItemSelected(item);
    }

    private void setupFilters(Menu menu) {
        final List<ConferenceDay> conferenceDays = conferenceManager.getConferenceDays();
        final MenuItem menuItem = menu.findItem(R.id.action_filter);
        final SubMenu subMenu = menuItem.getSubMenu();

        int index = 0;
        for (ConferenceDay confDay : conferenceDays) {
            final String name = SchedulePagerAdapter.formatDate(confDay.getDayMs());
            final int id = confDay.getName().hashCode();
            // The id of the menu item is just hashCode() on the conference day name.
            final MenuItem added = subMenu.add(id, id, index, name);
            added.setVisible(true).setCheckable(true).setChecked(true);
            index++;
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

    private void invalidateViewPager() {
        viewPager.setAdapter(schedulePagerAdapter);
        schedulePagerAdapter.notifyDataSetChanged();
        tabLayout.setupWithViewPager(viewPager);
    }

    private void onSearchQuery(String query) {
        scheduleLineupSearchManager.saveLastQuery(query);
        getMainActivity().sendBroadcast(new Intent(
                ScheduleLineupSearchManager.SEARCH_INTENT_ACTION));
    }
}

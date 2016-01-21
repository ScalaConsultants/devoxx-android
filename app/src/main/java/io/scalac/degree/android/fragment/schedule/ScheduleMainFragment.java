package io.scalac.degree.android.fragment.schedule;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import android.app.SearchManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.scalac.degree.android.adapter.schedule.SchedulePagerAdapter;
import io.scalac.degree.android.fragment.common.BaseFragment;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree.utils.DateUtils;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_schedules)
public class ScheduleMainFragment extends BaseFragment {

    @Bean
    SlotsDataManager slotsDataManager;

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

    private SchedulePagerAdapter schedulePagerAdapter;

    @AfterInject
    void afterInject() {
        final List<Long> days = Stream.of(slotsDataManager.getLastTalks())
                .groupBy(value -> DateUtils.calculateDayStartMs(value.fromTimeMillis))
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.<Long>toList());

        schedulePagerAdapter = new SchedulePagerAdapter(
                getChildFragmentManager(), days);
    }

    @AfterViews
    void afterViews() {
        viewPager.setAdapter(schedulePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
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

    private void onSearchQuery(String query) {
        // TODO Make search query.
    }

    // TODO TBD
    boolean onMondayClick(MenuItem item) {
        item.setChecked(!item.isChecked());

        // TODO Will be gethered from ConferenceMangager class.
        schedulePagerAdapter.refreshDays(new ArrayList<>(0));
        viewPager.setAdapter(schedulePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return true;
    }
}

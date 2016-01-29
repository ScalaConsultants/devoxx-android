package com.devoxx.android.fragment.common;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.MenuRes;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.devoxx.R;
import com.devoxx.android.activity.AboutActivity_;
import com.devoxx.android.activity.SettingsActivity_;
import com.devoxx.android.dialog.FiltersDialog;
import com.devoxx.data.schedule.filter.ScheduleFilterManager;
import com.devoxx.data.schedule.filter.model.RealmScheduleDayItemFilter;
import com.devoxx.data.schedule.filter.model.RealmScheduleTrackItemFilter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.SystemService;

import java.util.List;

@EFragment
public abstract class BaseMenuFragment extends BaseFragment
        implements FiltersDialog.IFiltersChangedListener {

    @SystemService
    protected SearchManager searchManager;

    @Bean
    protected ScheduleFilterManager scheduleFilterManager;

    @AfterViews
    protected void afterViews() {
        setHasOptionsMenu(true);
    }

    @MenuRes
    protected abstract int getMenuRes();

    protected abstract FiltersDialog.IFiltersChangedListener getFiltersListener();

    protected abstract void onSearchQuery(String query);

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(getMenuRes(), menu);
        setupFilterMenuIfNeeded(menu);
        setupSearchViewIfNeeded(menu);
        super.onCreateOptionsMenu(menu, inflater);
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
    }

    @Override
    public void onFiltersDismissed() {
        getActivity().supportInvalidateOptionsMenu();
        getMainActivity().sendBroadcast(new Intent(
                ScheduleFilterManager.FILTERS_CHANGED_ACTION));
    }

    @Override
    public void onFiltersDefault() {
        scheduleFilterManager.defaultFilters();
    }

    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.toolbar_menu_item_with_badge_view, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.count);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText(String.valueOf(count));
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        final Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    private void setupFilterMenuIfNeeded(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_filter);
        if (menuItem != null) {
            if (scheduleFilterManager.isSomeFiltersActive()) {
                final int activeFiltersCount = scheduleFilterManager.unselectedFiltersCount();
                menuItem.setIcon(buildCounterDrawable(activeFiltersCount, R.drawable.ic_filter_white_24px));
            } else {
                menu.findItem(R.id.action_filter).setIcon(R.drawable.ic_filter_outline_white_24px);
            }
        }
    }

    private void setupSearchViewIfNeeded(Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
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
    }

    @OptionsItem(R.id.action_filter)
    protected void onFilterClicked() {
        final List<RealmScheduleDayItemFilter> dayFilters = scheduleFilterManager.getDayFilters();
        final List<RealmScheduleTrackItemFilter> trackFilters = scheduleFilterManager.getTrackFilters();
        FiltersDialog.showFiltersDialog(getContext(), dayFilters, trackFilters, this);
    }

    @OptionsItem(R.id.action_settings)
    protected void onSettingsClick() {
        SettingsActivity_.intent(this).start();
    }

    @OptionsItem(R.id.action_about)
    protected void onAboutClick() {
        AboutActivity_.intent(this).start();
    }
}

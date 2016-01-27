package com.devoxx.android.fragment.map;

import android.annotation.SuppressLint;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import com.devoxx.R;
import com.devoxx.android.adapter.map.MapPagerAdapter;
import com.devoxx.android.view.NonSwipeableViewPager;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import java.util.Arrays;
import java.util.List;

@EFragment(R.layout.fragment_maps)
public class MapMainFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    @ViewById(R.id.tab_layout)
    TabLayout tabLayout;

    @ViewById(R.id.pager)
    NonSwipeableViewPager pager;

    @ColorRes(R.color.primary_text)
    int selectedTablColor;

    @ColorRes(R.color.tab_text_unselected)
    int unselectedTablColor;

    @ColorRes(R.color.primary_text)
    int tabStripColor;

    private MapPagerAdapter mapPagerAdapter;

    @AfterInject
    void afterInject() {
        // TODO Floors will be added from configuration.
        mapPagerAdapter = new MapPagerAdapter(getChildFragmentManager(), getFloors());
    }

    @AfterViews
    @SuppressLint("DefaultLocale")
    protected void afterViews() {
        tabLayout.setTabTextColors(unselectedTablColor, selectedTablColor);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setSelectedTabIndicatorColor(tabStripColor);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.venue));

        // TODO Floor will be added from configuration.
        for (Integer floor : getFloors()) {
            tabLayout.addTab(tabLayout.newTab().setText(String.format("%d FLOOR", floor)));
        }

        tabLayout.setOnTabSelectedListener(this);

        pager.setAdapter(mapPagerAdapter);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        // Nothing.
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        // Nothing.
    }

    private List<Integer> getFloors() {
        return Arrays.asList(1, 2);
    }
}

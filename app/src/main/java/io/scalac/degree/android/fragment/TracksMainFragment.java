package io.scalac.degree.android.fragment;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import io.scalac.degree.android.adapter.TracksPagerAdapter;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_tracks)
public class TracksMainFragment extends BaseFragment {

    @Bean
    SlotsDataManager slotsDataManager;

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
        tracksPagerAdapter = new TracksPagerAdapter(getChildFragmentManager(),
                slotsDataManager.getLastTalks());
    }

    @AfterViews
    void afterViews() {
        viewPager.setAdapter(tracksPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(unselectedTablColor, selectedTablColor);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setSelectedTabIndicatorColor(tabStripColor);
    }
}

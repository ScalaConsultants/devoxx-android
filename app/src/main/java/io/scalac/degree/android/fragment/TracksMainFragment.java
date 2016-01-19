package io.scalac.degree.android.fragment;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.List;
import java.util.Map;

import io.scalac.degree.connection.model.SlotApiModel;
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

    public class TracksPagerAdapter extends FragmentPagerAdapter {

        private final Map<String, List<SlotApiModel>> tracksMap;
        private final List<String> tracksNames;

        public TracksPagerAdapter(FragmentManager fm, List<SlotApiModel> slots) {
            super(fm);

            tracksMap = Stream.of(slots).filter(new Predicate<SlotApiModel>() {
                @Override
                public boolean test(SlotApiModel slot) {
                    return slot.talk != null;
                }
            }).collect(Collectors.groupingBy(new Function<SlotApiModel, String>() {
                @Override
                public String apply(SlotApiModel value) {
                    return value.talk.track;
                }
            }));

            tracksNames = Stream.of(tracksMap.keySet()).
                    sorted().collect(Collectors.<String>toList());
        }

        @Override
        public Fragment getItem(int position) {

            return TracksListFragment_.builder().trackName(tracksNames.get(position)).build();
        }

        @Override
        public int getCount() {
            return tracksMap.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tracksNames.get(position);
        }
    }
}

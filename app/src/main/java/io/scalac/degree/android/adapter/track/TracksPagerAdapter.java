package io.scalac.degree.android.adapter.track;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;
import java.util.Map;

import io.scalac.degree.android.fragment.track.TracksListFragment_;
import io.scalac.degree.connection.model.SlotApiModel;

public class TracksPagerAdapter extends FragmentPagerAdapter {

    private final Map<String, List<SlotApiModel>> tracksMap;
    private final List<String> tracksNames;

    public TracksPagerAdapter(FragmentManager fm, List<SlotApiModel> slots) {
        super(fm);

        tracksMap = Stream.of(slots).filter(slot -> slot.talk != null)
                .collect(Collectors.groupingBy(new Function<SlotApiModel, String>() {
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

package io.scalac.degree.android.adapter.track;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;
import java.util.Map;

import io.scalac.degree.android.fragment.track.TracksListFragment_;
import io.scalac.degree.connection.model.SlotApiModel;

public class TracksPagerAdapter extends FragmentStatePagerAdapter {

    private Map<String, List<SlotApiModel>> tracksMap;
    private List<String> tracksNames;

    public TracksPagerAdapter(FragmentManager fm, List<SlotApiModel> slots) {
        super(fm);
        setData(slots);
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

    public void setData(List<SlotApiModel> slotApiModels) {
        tracksMap = Stream.of(slotApiModels).filter(slot -> slot.talk != null)
                .collect(Collectors.groupingBy(new Function<SlotApiModel, String>() {
                    @Override
                    public String apply(SlotApiModel value) {
                        return value.talk.track;
                    }
                }));

        tracksNames = Stream.of(tracksMap.keySet()).
                sorted().collect(Collectors.<String>toList());
    }
}

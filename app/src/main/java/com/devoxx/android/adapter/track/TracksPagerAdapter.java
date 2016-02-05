package com.devoxx.android.adapter.track;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.devoxx.android.fragment.track.TracksListFragment_;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.utils.tuple.Tuple;

public class TracksPagerAdapter extends FragmentStatePagerAdapter {

    private Map<Tuple<String, String>, List<SlotApiModel>> tracksMap;
    private List<String> tracksNames;
    private HashMap<String, String> nameIdMap;

    public TracksPagerAdapter(FragmentManager fm, List<SlotApiModel> slots) {
        super(fm);
        setData(slots);
    }

    @Override
    public Fragment getItem(int position) {
        final String trackname = tracksNames.get(position);
        final String trackId = nameIdMap.get(trackname);
        return TracksListFragment_.builder().trackId(trackId).build();
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
                .collect(Collectors.groupingBy(new Function<SlotApiModel, Tuple<String, String>>() {
                    @Override
                    public Tuple<String, String> apply(SlotApiModel value) {
                        return new Tuple<>(value.talk.track, value.talk.trackId);
                    }
                }));

        final List<Tuple<String, String>> nameIdMapping = new ArrayList<>(tracksMap.keySet());
        nameIdMap = new HashMap<>();
        for (Tuple<String, String> tuple : nameIdMapping) {
            nameIdMap.put(tuple.first, tuple.second);
        }

        tracksNames = Stream.of(nameIdMap.keySet())
                .sorted().collect(Collectors.<String>toList());
    }
}

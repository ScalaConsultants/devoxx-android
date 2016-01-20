package io.scalac.degree.android.fragment.schedule;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.scalac.degree.android.adapter.schedule.ScheduleDayLineupAdapter;
import io.scalac.degree.android.adapter.schedule.model.BreakScheduleItem;
import io.scalac.degree.android.adapter.schedule.model.ScheduleItem;
import io.scalac.degree.android.adapter.schedule.model.TalksScheduleItem;
import io.scalac.degree.android.fragment.common.BaseListFragment;
import io.scalac.degree.android.fragment.talk.TalkFragment_;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree.utils.InfoUtil;
import io.scalac.degree.utils.Logger;
import io.scalac.degree.utils.tuple.TripleTuple;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_list)
public class ScheduleDayLinupFragment extends BaseListFragment {

    private static final long UNKNOWN_LINEUP_TIME = -1;

    @FragmentArg
    long lineupDayMs = UNKNOWN_LINEUP_TIME;

    @Bean
    SlotsDataManager slotsDataManager;

    @Bean
    ScheduleDayLineupAdapter scheduleDayLineupAdapter;

    @Bean
    InfoUtil infoUtil;

    @AfterInject
    void afterInject() {
        if (lineupDayMs == UNKNOWN_LINEUP_TIME) {
            throw new RuntimeException("Lineup day time must be provided!");
        }

        final List<SlotApiModel> slotsRaw = slotsDataManager.getSlotsForDay(lineupDayMs);
        final Map<TripleTuple<Long, Long, String>, List<SlotApiModel>> map = Stream.of(slotsRaw)
                .sortBy(new Function<SlotApiModel, Comparable>() {
                    @Override
                    public Comparable apply(SlotApiModel value) {
                        return value.fromTimeMillis;
                    }
                })
                .collect(Collectors.groupingBy(new Function<SlotApiModel, TripleTuple<Long, Long, String>>() {
                    @Override
                    public TripleTuple<Long, Long, String> apply(SlotApiModel value) {
                        return new TripleTuple<>(value.fromTimeMillis, value.toTimeMillis, value.slotId);
                    }
                }));

        final List<TripleTuple<Long, Long, String>> sortedKeys = Stream.of(map.keySet())
                .sortBy(new Function<TripleTuple<Long, Long, String>, Comparable>() {
                    @Override
                    public Comparable apply(TripleTuple<Long, Long, String> value) {
                        return value.first;
                    }
                })
                .collect(Collectors.<TripleTuple<Long, Long, String>>toList());

        final List<ScheduleItem> items = new ArrayList<>(sortedKeys.size());

        int index = 0;
        for (TripleTuple<Long, Long, String> sortedKey : sortedKeys) {
            final long startTime = sortedKey.first;
            final long endTime = sortedKey.second;

            final List<SlotApiModel> models = map.get(sortedKey);
            final int size = models.size();

            if (isBreak(models)) {
                items.add(new BreakScheduleItem(
                        startTime, endTime, index, index, models));
            } else {
                final TalksScheduleItem talksScheduleItem = new TalksScheduleItem(
                        startTime, endTime, index, index + size);

                index += 1;

                talksScheduleItem.setOtherSlots(models);
                items.add(talksScheduleItem);
            }

            index += size;
        }

        scheduleDayLineupAdapter.setData(items);
    }

    private boolean isBreak(List<SlotApiModel> models) {
        boolean result = false;
        for (SlotApiModel model : models) {
            if (model.isBreak()) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return scheduleDayLineupAdapter;
    }

    @Override
    public void onItemClick(RecyclerView parent, View view, int position, long id) {
        final SlotApiModel slotApiModel = scheduleDayLineupAdapter.getClickedSlot(position);
        Logger.l("Clicked slot: " + slotApiModel);

        if (slotApiModel.isTalk()) {
            getMainActivity().replaceFragment(TalkFragment_.builder()
                    .slotModel(slotApiModel).build(), true);
        } else {
            infoUtil.showToast("Nothing here...");
        }
    }
}

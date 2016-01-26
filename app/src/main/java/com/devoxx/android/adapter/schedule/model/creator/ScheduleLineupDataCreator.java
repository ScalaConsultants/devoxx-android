package com.devoxx.android.adapter.schedule.model.creator;

import com.annimon.stream.Collector;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.devoxx.android.adapter.schedule.model.BreakScheduleItem;
import com.devoxx.android.adapter.schedule.model.ScheduleItem;
import com.devoxx.android.adapter.schedule.model.TalksScheduleItem;
import com.devoxx.data.manager.NotificationsManager;
import com.devoxx.data.manager.SlotsDataManager;
import com.devoxx.utils.tuple.TripleTuple;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.devoxx.connection.model.SlotApiModel;

@EBean
public class ScheduleLineupDataCreator {

    @Bean
    SlotsDataManager slotsDataManager;

    @Bean
    NotificationsManager notificationsManager;

    private Collector<SlotApiModel, ?, Map<TripleTuple<Long, Long, String>, List<SlotApiModel>>>
            triplesCollector = createTriplesCollector();

    private Function<SlotApiModel, Comparable>
            sortTriplesPredicate = value -> value.fromTimeMillis;

    private Function<? super TripleTuple<Long, Long, String>, ? extends Comparable>
            sortKeysPredicate = value -> value.first;

    @NonNull
    public List<ScheduleItem> prepareInitialData(long lineupDayMs) {
        final List<SlotApiModel> slotsRaw = slotsDataManager.getSlotsForDay(lineupDayMs);
        return prepareResult(slotsRaw);
    }

    @NonNull
    public List<ScheduleItem> prepareResult(List<SlotApiModel> slotApiModels) {
        final Map<TripleTuple<Long, Long, String>, List<SlotApiModel>> map = Stream.of(slotApiModels)
                .sortBy(sortTriplesPredicate)
                .collect(triplesCollector);

        final List<TripleTuple<Long, Long, String>> sortedKeys = Stream.of(map.keySet())
                .sortBy(sortKeysPredicate)
                .collect(Collectors.<TripleTuple<Long, Long, String>>toList());

        return buildListItems(map, sortedKeys);
    }

    @NonNull
    private List<ScheduleItem> buildListItems(
            Map<TripleTuple<Long, Long, String>, List<SlotApiModel>> map,
            List<TripleTuple<Long, Long, String>> sortedKeys) {
        final List<ScheduleItem> result = new ArrayList<>(sortedKeys.size());

        int index = 0;
        for (TripleTuple<Long, Long, String> sortedKey : sortedKeys) {
            final long startTime = sortedKey.first;
            final long endTime = sortedKey.second;

            final List<SlotApiModel> models = map.get(sortedKey);
            final int size = models.size();

            if (isBreak(models)) {
                result.add(new BreakScheduleItem(
                        startTime, endTime, index, index, models));
            } else {
                final int endIndex = index + size + 1; // 1 - for more view.
                final TalksScheduleItem talksScheduleItem = new TalksScheduleItem(
                        startTime, endTime, index, endIndex);

                index += 2; // +2 for timespan and more view.

                for (SlotApiModel model : models) {
                    if (notificationsManager.isNotificationScheduled(model.slotId)) {
                        talksScheduleItem.addFavouredSlot(model);
                    } else {
                        talksScheduleItem.addOtherSlot(model);
                    }
                }

                result.add(talksScheduleItem);
            }

            index += size;
        }

        return result;
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

    private static Collector<SlotApiModel, ?,
            Map<TripleTuple<Long, Long, String>, List<SlotApiModel>>> createTriplesCollector() {
        return Collectors.groupingBy(new Function<SlotApiModel, TripleTuple<Long, Long, String>>() {
            @Override
            public TripleTuple<Long, Long, String> apply(SlotApiModel value) {
                return new TripleTuple<>(value.fromTimeMillis, value.toTimeMillis, value.slotId);
            }
        });
    }

    public void refreshIndexes(List<ScheduleItem> data) {
        int index = 0;
        for (ScheduleItem scheduleItem : data) {
            if (scheduleItem instanceof BreakScheduleItem) {
                scheduleItem.setStartIndex(index);
                scheduleItem.setStopIndex(index);
                index++;
            } else {
                final int talkItemSize = scheduleItem.getSize();
                scheduleItem.setStartIndex(index);
                scheduleItem.setStopIndex(index + talkItemSize - 1);
                index += talkItemSize;
            }
        }
    }
}

package io.scalac.degree.android.adapter.schedule.model.creator;

import com.annimon.stream.Collector;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.scalac.degree.android.adapter.schedule.model.BreakScheduleItem;
import io.scalac.degree.android.adapter.schedule.model.ScheduleItem;
import io.scalac.degree.android.adapter.schedule.model.TalksScheduleItem;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree.utils.tuple.TripleTuple;

@EBean
public class ScheduleLineupDataCreator {

    @Bean
    SlotsDataManager slotsDataManager;

    private SearchPredicate searchPredicate;
    private Function<SlotApiModel, Comparable> sortTriplesPredicate =
            new Function<SlotApiModel, Comparable>() {
                @Override
                public Comparable apply(SlotApiModel value) {
                    return value.fromTimeMillis;
                }
            };

    private Collector<SlotApiModel, ?, Map<TripleTuple<Long, Long, String>, List<SlotApiModel>>> triplesCollector
            = Collectors.groupingBy(new Function<SlotApiModel, TripleTuple<Long, Long, String>>() {
        @Override
        public TripleTuple<Long, Long, String> apply(SlotApiModel value) {
            return new TripleTuple<>(value.fromTimeMillis, value.toTimeMillis, value.slotId);
        }
    });

    private Function<? super TripleTuple<Long, Long, String>, ? extends Comparable> sortKeysPredicate
            = new Function<TripleTuple<Long, Long, String>, Comparable>() {
        @Override
        public Comparable apply(TripleTuple<Long, Long, String> value) {
            return value.first;
        }
    };

    @AfterInject
    void afterInject() {
        searchPredicate = new SearchPredicate();
    }

    public List<ScheduleItem> prepareInitialData(long lineupDayMs) {
        final List<SlotApiModel> slotsRaw = slotsDataManager.getSlotsForDay(lineupDayMs);
        return prepareResult(slotsRaw);
    }

    public List<ScheduleItem> handleSearchQuery(long lineupDayMs, final String query) {
        final String internalQuery = query.toLowerCase();
        searchPredicate.setQuery(internalQuery);

        final List<SlotApiModel> slotsRaw = slotsDataManager.getSlotsForDay(lineupDayMs);
        final List<SlotApiModel> queriedRaw = Stream.of(slotsRaw)
                .filter(searchPredicate)
                .collect(Collectors.<SlotApiModel>toList());
        return prepareResult(queriedRaw);
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

    private List<ScheduleItem> prepareResult(List<SlotApiModel> slotApiModels) {
        final Map<TripleTuple<Long, Long, String>, List<SlotApiModel>> map = Stream.of(slotApiModels)
                .sortBy(sortTriplesPredicate)
                .collect(triplesCollector);

        final List<TripleTuple<Long, Long, String>> sortedKeys = Stream.of(map.keySet())
                .sortBy(sortKeysPredicate)
                .collect(Collectors.<TripleTuple<Long, Long, String>>toList());

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
                final TalksScheduleItem talksScheduleItem = new TalksScheduleItem(
                        startTime, endTime, index, index + size);

                index += 1;

                talksScheduleItem.setOtherSlots(models);
                result.add(talksScheduleItem);
            }

            index += size;
        }

        return result;
    }

    private static class SearchPredicate implements Predicate<SlotApiModel> {

        private String query;

        @Override
        public boolean test(SlotApiModel value) {
            return testQuery(value, query);
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public boolean testQuery(SlotApiModel model, String query) {
            boolean result = false;
            if (model.isTalk()) {
                result = model.talk.title.toLowerCase().contains(query)
                        || model.talk.track.toLowerCase().contains(query)
                        || model.talk.summary.toLowerCase().contains(query);
            } else if (model.isBreak()) {
                result = model.slotBreak.nameEN.toLowerCase().contains(query);
            }
            return result;
        }
    }
}

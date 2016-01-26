package com.devoxx.data.schedule.search;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.devoxx.android.adapter.schedule.model.ScheduleItem;
import com.devoxx.android.adapter.schedule.model.creator.ScheduleLineupDataCreator;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.data.manager.SlotsDataManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.support.annotation.NonNull;

import java.util.List;

@EBean
public class ScheduleLineupSearchManager {

    public static final String SEARCH_INTENT_ACTION = "search_intent_action";

    @Bean
    SlotsDataManager slotsDataManager;

    @Bean
    ScheduleLineupDataCreator scheduleLineupDataCreator;

    @Pref
    ScheduleSearchStore_ scheduleSearchStore;

    private SearchPredicate searchPredicate = new SearchPredicate();

    @NonNull
    public List<ScheduleItem> handleSearchQuery(long lineupDayMs, final String query) {
        final String lastQuery = formatQuery(query);
        searchPredicate.setQuery(lastQuery);

        final List<SlotApiModel> slotsRaw = slotsDataManager.getSlotsForDay(lineupDayMs);
        final List<SlotApiModel> queriedRaw = Stream.of(slotsRaw)
                .filter(searchPredicate)
                .collect(Collectors.<SlotApiModel>toList());
        return scheduleLineupDataCreator.prepareResult(queriedRaw);
    }

    public String getLastQuery() {
        return scheduleSearchStore.lastQuery().getOr("");
    }

    public void saveLastQuery(String lastQuery) {
        final String finalQuery = formatQuery(lastQuery);
        scheduleSearchStore.lastQuery().put(finalQuery);
    }

    private String formatQuery(String query) {
        return query.trim().toLowerCase();
    }

    public void clearLastQuery() {
        saveLastQuery("");
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

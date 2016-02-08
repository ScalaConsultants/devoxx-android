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
public class SearchManager {

    public static final String SEARCH_INTENT_ACTION = "search_intent_action";

    @Bean
    SlotsDataManager slotsDataManager;

    @Bean
    ScheduleLineupDataCreator scheduleLineupDataCreator;

    @Pref
    ScheduleSearchStore_ scheduleSearchStore;

    @NonNull
    public List<ScheduleItem> handleSearchQuery(long lineupDayMs, final String query) {
        final String lastQuery = formatQuery(query);
        final SlotApiModel.FilterPredicate fp = new SlotApiModel.FilterPredicate(lastQuery);

        final List<SlotApiModel> slotsRaw = slotsDataManager.getSlotsForDay(lineupDayMs);
        final List<SlotApiModel> queriedRaw = Stream.of(slotsRaw)
                .filter(fp)
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
}

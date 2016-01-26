package com.devoxx.data.schedule.filter;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.devoxx.android.adapter.schedule.model.ScheduleItem;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;

import com.devoxx.android.adapter.schedule.model.TalksScheduleItem;
import com.devoxx.android.adapter.schedule.model.creator.ScheduleLineupDataCreator;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.data.RealmProvider;
import com.devoxx.data.conference.model.ConferenceDay;
import com.devoxx.data.model.RealmTrack;
import com.devoxx.data.schedule.filter.model.RealmScheduleDayItemFilter;
import com.devoxx.data.schedule.filter.model.RealmScheduleTrackItemFilter;

@EBean
public class ScheduleFilterManager {

    public static final String FILTERS_CHANGED_ACTION = "filters_changed_action";

    /**
     * Column isActive in {@link RealmScheduleDayItemFilter} and
     * {@link RealmScheduleTrackItemFilter}
     */
    private static final String IS_ACTIVE_COLUMN_NAME = "isActive";

    @Bean
    ScheduleLineupDataCreator scheduleLineupDataCreator;

    @Bean
    RealmProvider realmProvider;

    public List<RealmScheduleDayItemFilter> getActiveDayFilters() {
        return getFilters(RealmScheduleDayItemFilter.class, true);
    }

    public List<RealmScheduleTrackItemFilter> getActiveTrackFilters() {
        return getFilters(RealmScheduleTrackItemFilter.class, true);
    }

    private List getFilters(Class<? extends RealmObject> clazz, boolean isActive) {
        final Realm realm = realmProvider.getRealm();
        final List result = realm.where(clazz).equalTo(IS_ACTIVE_COLUMN_NAME, isActive).findAll();
        realm.close();
        return result;
    }

    public void createDayFiltersDefinition(List<ConferenceDay> conferenceDays) {
        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        realm.allObjects(RealmScheduleDayItemFilter.class).clear();
        realm.commitTransaction();

        realm.beginTransaction();
        for (ConferenceDay conferenceDay : conferenceDays) {
            final RealmScheduleDayItemFilter item = realm
                    .createObject(RealmScheduleDayItemFilter.class);
            item.setActive(true);
            item.setDayMs(conferenceDay.getDayMs());
            item.setLabel(conferenceDay.getName());
        }
        realm.commitTransaction();

        realm.close();
    }

    public void createTrackFiltersDefinition(List<RealmTrack> tracks) {
        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        realm.allObjects(RealmScheduleTrackItemFilter.class).clear();
        realm.commitTransaction();

        realm.beginTransaction();
        for (RealmTrack track : tracks) {
            final RealmScheduleTrackItemFilter item = realm
                    .createObject(RealmScheduleTrackItemFilter.class);
            item.setActive(true);
            item.setLabel(track.getTitle());
            item.setTrackName(track.getId());
        }
        realm.commitTransaction();

        realm.close();
    }

    public List<RealmScheduleTrackItemFilter> getTrackFilters() {
        final Realm realm = realmProvider.getRealm();
        final List<RealmScheduleTrackItemFilter> result
                = realm.allObjects(RealmScheduleTrackItemFilter.class);
        realm.close();
        return result;
    }

    public List<RealmScheduleDayItemFilter> getDayFilters() {
        final Realm realm = realmProvider.getRealm();
        final List<RealmScheduleDayItemFilter> result
                = realm.allObjects(RealmScheduleDayItemFilter.class);
        realm.close();
        return result;
    }

    public void updateFilter(RealmScheduleDayItemFilter itemFilter, boolean isActive) {
        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        itemFilter.setActive(isActive);
        realm.copyToRealmOrUpdate(itemFilter);
        realm.commitTransaction();
        realm.close();
    }

    public void updateFilter(RealmScheduleTrackItemFilter itemFilter, boolean isActive) {
        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        itemFilter.setActive(isActive);
        realm.copyToRealmOrUpdate(itemFilter);
        realm.commitTransaction();
        realm.close();
    }

    public void clearFilters() {
        setAllFiltersEnabled(false);
    }

    public void defaultFilters() {
        setAllFiltersEnabled(true);
    }

    public List<ScheduleItem> applyTracksFilter(List<ScheduleItem> items) {
        final List<RealmScheduleTrackItemFilter> activeFilters = getActiveTrackFilters();
        final List<RealmScheduleTrackItemFilter> allTrackFilters = getTrackFilters();

        List<ScheduleItem> result = items;
        if (activeFilters.size() != allTrackFilters.size()) {
            final List<SlotApiModel> filteredModels = Stream.of(items)
                    .filter(value -> value instanceof TalksScheduleItem)
                    .flatMap(value -> Stream.of(value.getAllItems()))
                    .filter(value -> {
                        if (value.isTalk()) {
                            for (RealmScheduleTrackItemFilter filter : activeFilters) {
                                if (value.talk.track.toLowerCase()
                                        .equalsIgnoreCase(filter.getTrackName().toLowerCase())
                                        || value.talk.track.toLowerCase()
                                        .equalsIgnoreCase(filter.getLabel().toLowerCase())) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
            result = scheduleLineupDataCreator.prepareResult(filteredModels);
        }
        return result;
    }

    private void setAllFiltersEnabled(boolean enabled) {
        final Realm realm = realmProvider.getRealm();
        final List<RealmScheduleDayItemFilter> days =
                realm.allObjects(RealmScheduleDayItemFilter.class);
        final List<RealmScheduleTrackItemFilter> tracks =
                realm.allObjects(RealmScheduleTrackItemFilter.class);
        realm.beginTransaction();
        for (int i = 0; i < days.size(); i++) {
            days.get(i).setActive(enabled);
        }
        for (int i = 0; i < tracks.size(); i++) {
            tracks.get(i).setActive(enabled);
        }
        realm.commitTransaction();
        realm.close();
    }

    public boolean isSomeFiltersActive() {
        return !getFilters(RealmScheduleDayItemFilter.class, false).isEmpty()
                || !getFilters(RealmScheduleTrackItemFilter.class, false).isEmpty();
    }
}

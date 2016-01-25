package io.scalac.degree.data.schedule.filter;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.List;

import io.realm.Realm;
import io.scalac.degree.data.RealmProvider;
import io.scalac.degree.data.conference.model.ConferenceDay;
import io.scalac.degree.data.model.RealmTrack;
import io.scalac.degree.data.schedule.filter.model.RealmScheduleDayItemFilter;
import io.scalac.degree.data.schedule.filter.model.RealmScheduleTrackItemFilter;

@EBean
public class ScheduleFilterManager {

    @Bean
    RealmProvider realmProvider;

    public List<RealmScheduleDayItemFilter> getActiveDayFilters() {
        final Realm realm = realmProvider.getRealm();
        final List<RealmScheduleDayItemFilter> result = realm
                .where(RealmScheduleDayItemFilter.class)
                .equalTo("isActive", true).findAll();
        realm.close();
        return result;
    }

    public List<RealmScheduleTrackItemFilter> getActiveTrackFilters() {
        final Realm realm = realmProvider.getRealm();
        final List<RealmScheduleTrackItemFilter> result = realm
                .where(RealmScheduleTrackItemFilter.class)
                .equalTo("isActive", true).findAll();
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
        final Realm realm = realmProvider.getRealm();
        final List<RealmScheduleDayItemFilter> days =
                realm.allObjects(RealmScheduleDayItemFilter.class);
        final List<RealmScheduleTrackItemFilter> tracks =
                realm.allObjects(RealmScheduleTrackItemFilter.class);
        realm.beginTransaction();
        for (int i = 0; i < days.size(); i++) {
            days.get(i).setActive(false);
        }
        for (int i = 0; i < tracks.size(); i++) {
            tracks.get(i).setActive(false);
        }
        realm.commitTransaction();
        realm.close();
    }
}

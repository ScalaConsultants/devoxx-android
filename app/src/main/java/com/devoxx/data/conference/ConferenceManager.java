package com.devoxx.data.conference;

import com.annimon.stream.Stream;
import com.devoxx.connection.cfp.model.ConferenceApiModel;
import com.devoxx.data.RealmProvider;
import com.devoxx.data.cache.BaseCache;
import com.devoxx.data.conference.model.ConferenceDay;
import com.devoxx.data.downloader.ConferenceDownloader;
import com.devoxx.data.downloader.TracksDownloader;
import com.devoxx.data.manager.SlotsDataManager;
import com.devoxx.data.manager.SpeakersDataManager;
import com.devoxx.data.model.RealmConference;
import com.devoxx.data.schedule.filter.ScheduleFilterManager;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;

@EBean(scope = EBean.Scope.Singleton)
public class ConferenceManager {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public interface IConferencesListener {

        void onConferencesDataStart();

        void onConferencesAvailable(List<ConferenceApiModel> conferenceS);

        void onConferencesError();
    }

    public interface IConferenceDataListener {
        void onConferenceDataStart();

        void onConferenceDataAvailable();

        void onConferenceDataError();
    }

    @Bean
    ConferenceDownloader conferenceDownloader;

    @Bean
    ScheduleFilterManager scheduleFilterManager;

    @Bean
    SlotsDataManager slotsDataManager;

    @Bean
    SpeakersDataManager speakersDataManager;

    @Bean
    TracksDownloader tracksDownloader;

    @Bean
    RealmProvider realmProvider;

    @Bean
    BaseCache baseCache;

    private List<ConferenceDay> conferenceDays;

    @Background
    public void fetchAvailableConferences(IConferencesListener listener) {
        try {
            notifyConferencesListenerAboutStart(listener);
            final List<ConferenceApiModel> conferences = conferenceDownloader.fetchAllConferences();
            notifyConferencesListenerSuccess(listener, conferences);
        } catch (IOException e) {
            notifyConferencesListenerError(listener);
        }
    }

    public void warmUp() {
        conferenceDownloader.warmUp();
    }

    @UiThread
    void notifyConferencesListenerAboutStart(IConferencesListener listener) {
        listener.onConferencesDataStart();
    }

    @UiThread
    void notifyConferencesListenerSuccess(
            IConferencesListener listener, List<ConferenceApiModel> list) {
        listener.onConferencesAvailable(list);
    }

    @UiThread
    void notifyConferencesListenerError(
            IConferencesListener listener) {
        listener.onConferencesError();
    }

    @Background
    public void fetchConferenceData(
            ConferenceApiModel conferenceApiModel,
            IConferenceDataListener listener) {
        final String confCode = conferenceApiModel.id;
        try {
            notifyConferenceListenerStart(listener);
            tracksDownloader.downloadTracksDescriptions(confCode);
            slotsDataManager.fetchTalksSync(confCode);
            speakersDataManager.fetchSpeakersSync(confCode);
            final List<ConferenceDay> conferenceDays = getConferenceDays();
            scheduleFilterManager.createDayFiltersDefinition(conferenceDays);
            saveActiveConference(conferenceApiModel);
            notifyConferenceListenerSuccess(listener);
        } catch (IOException e) {
            clearCurrentConferenceData();
            notifyConferenceListenerError(listener);
        }
    }

    @UiThread
    void notifyConferenceListenerStart(IConferenceDataListener listener) {
        listener.onConferenceDataStart();
    }

    @UiThread
    void notifyConferenceListenerSuccess(IConferenceDataListener listener) {
        listener.onConferenceDataAvailable();
    }

    @UiThread
    void notifyConferenceListenerError(IConferenceDataListener listener) {
        listener.onConferenceDataError();
    }

    public List<ConferenceDay> getConferenceDays() {
        final RealmConference realmConference = getActiveConference();

        // TODO Take dates from model!
        final String fromDate = "2015-11-09T01:00:00.000Z";
        final String toDate = "2015-11-13T23:00:00.000Z";
        final DateTime fromConfDate = parseConfDate(fromDate);
        final DateTime toConfDate = parseConfDate(toDate);
        final DateTime now = new DateTime(getNow());

        final int daysSpan = Days.daysBetween(fromConfDate, toConfDate).getDays();

        final List<ConferenceDay> result = new ArrayList<>(daysSpan + 1 /* include days */);
        for (int i = 0; i <= daysSpan; i++) {
            final DateTime tmpDate = fromConfDate.plusDays(i);
            final boolean isToday = tmpDate.getDayOfYear() == now.getDayOfYear();
            result.add(new ConferenceDay(
                    tmpDate.getMillis(),
                    tmpDate.dayOfWeek().getAsText(Locale.getDefault()),
                    isToday));
        }

        conferenceDays = new ArrayList<>(result);

        return result;
    }

    public boolean isConferenceChoosen() {
        return getActiveConference() != null;
    }

    public RealmConference getActiveConference() {
        final Realm realm = realmProvider.getRealm();
        return realm.where(RealmConference.class).findFirst();
    }

    public String getActiveConferenceId() {
        return getActiveConference().getId();
    }

    public void clearCurrentConferenceData() {
        clearCurrentConference();
        clearSlotsData();
        clearTracksData();
        clearSpeakersData();
        clearFiltersDefinitions();
        clearCache();
    }

    public ConferenceDay getCurrentConfDay() {
        return Stream.of(conferenceDays).filter(ConferenceDay::isRunning).findFirst().get();
    }

    public long getNow() {
        //TODO Tests!
        final String test = "2015-11-10T10:00:00.000Z";
        return parseConfDate(test).getMillis();
    }

    public boolean isFutureConference(ConferenceApiModel conference) {
//        return parseConfDate(conference.fromDate).getMillis() > System.currentTimeMillis();
        // TODO Tests!
        return !conference.country.toLowerCase().contains("belg");
    }

    private void clearFiltersDefinitions() {
        scheduleFilterManager.removeAllFilters();
    }

    private void clearSpeakersData() {
        speakersDataManager.clearData();
    }

    private void clearTracksData() {
        tracksDownloader.clearTracksData();
    }

    private void clearCache() {
        baseCache.clearAllCache();
    }

    private void clearSlotsData() {
        slotsDataManager.clearData();
    }

    private void clearCurrentConference() {
        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        realm.where(RealmConference.class).findAll().clear();
        realm.commitTransaction();
        realm.close();
    }

    private void saveActiveConference(ConferenceApiModel conferenceApiModel) {
        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        realm.allObjects(RealmConference.class).clear();
        realm.copyToRealmOrUpdate(new RealmConference(conferenceApiModel));
        realm.commitTransaction();
        realm.close();
    }

    public static DateTime parseConfDate(String stringDate) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_FORMAT)
                .withZone(DateTimeZone.forID("Europe/Paris"));
        return formatter.parseDateTime(stringDate);
    }
}

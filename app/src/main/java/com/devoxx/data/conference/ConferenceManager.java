package com.devoxx.data.conference;

import com.devoxx.connection.cfp.model.ConferenceApiModel;
import com.devoxx.data.RealmProvider;
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
        saveActiveConference(conferenceApiModel);

        final String confCode = conferenceApiModel.id;
        try {
            notifyConferenceListenerStart(listener);
            tracksDownloader.downloadTracksDescriptions(confCode);
            slotsDataManager.fetchTalksSync(confCode);
            speakersDataManager.fetchSpeakersSync(confCode);
            final List<ConferenceDay> conferenceDays = getConferenceDays();
            scheduleFilterManager.createDayFiltersDefinition(conferenceDays);
            notifyConferenceListenerSuccess(listener);
        } catch (IOException e) {
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
        final DateTime fromConfDate = convertStringDate(fromDate);
        final DateTime toConfDate = convertStringDate(toDate);

        final int daysSpan = Days.daysBetween(fromConfDate, toConfDate).getDays();

        final List<ConferenceDay> result = new ArrayList<>(daysSpan + 1 /* include days */);
        for (int i = 0; i <= daysSpan; i++) {
            final DateTime tmpDate = fromConfDate.plusDays(i);
            result.add(new ConferenceDay(tmpDate.getMillis(),
                    tmpDate.dayOfWeek().getAsText(Locale.getDefault())));
        }

        return result;
    }

    private void saveActiveConference(ConferenceApiModel conferenceApiModel) {
        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        realm.allObjects(RealmConference.class).clear();
        realm.copyToRealmOrUpdate(new RealmConference(conferenceApiModel));
        realm.commitTransaction();
        realm.close();
    }

    private RealmConference getActiveConference() {
        final Realm realm = realmProvider.getRealm();
        final RealmConference result = realm.where(RealmConference.class).findFirst();
        realm.close();
        return result;
    }

    private DateTime convertStringDate(String stringDate) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_FORMAT);
        return formatter.parseDateTime(stringDate);
    }
}

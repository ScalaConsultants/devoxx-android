package com.devoxx.data.conference;

import android.content.Context;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.devoxx.connection.cfp.model.ConferenceApiModel;
import com.devoxx.data.DataInformation;
import com.devoxx.data.DataInformation_;
import com.devoxx.data.RealmProvider;
import com.devoxx.data.Settings;
import com.devoxx.data.Settings_;
import com.devoxx.data.cache.BaseCache;
import com.devoxx.data.conference.model.ConferenceDay;
import com.devoxx.data.downloader.ConferenceDownloader;
import com.devoxx.data.downloader.TracksDownloader;
import com.devoxx.data.manager.SlotsDataManager;
import com.devoxx.data.manager.SpeakersDataManager;
import com.devoxx.data.model.RealmConference;
import com.devoxx.data.schedule.filter.ScheduleFilterManager;
import com.devoxx.data.user.UserManager;
import com.devoxx.utils.Logger;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.lang.ref.WeakReference;
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

        void onConferenceDataAvailable(boolean isAnyTalks);

        void onConferenceDataError();

    }

    @RootContext
    Context context;

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

    @Bean
    UserManager userManager;

    @Pref
    Settings_ settings;

    private List<ConferenceDay> conferenceDays;

    @Background
    public void fetchAvailableConferences() {
        try {
            notifyConferencesListenerAboutStart(allConferencesDataListener);
            final List<ConferenceApiModel> conferences = conferenceDownloader.fetchAllConferences();
            notifyConferencesListenerSuccess(allConferencesDataListener, conferences);
        } catch (IOException e) {
            notifyConferencesListenerError(allConferencesDataListener);
        }
    }

    @UiThread
    void notifyConferencesListenerAboutStart(WeakReference<IConferencesListener> listener) {
        isDownloadingAllConferencesData = true;
        final IConferencesListener internalListener = listener.get();
        if (internalListener != null) {
            internalListener.onConferencesDataStart();
        }
    }

    @UiThread
    void notifyConferencesListenerSuccess(
            WeakReference<IConferencesListener> listener, List<ConferenceApiModel> list) {
        isDownloadingAllConferencesData = false;
        final IConferencesListener internalListener = listener.get();
        if (internalListener != null) {
            internalListener.onConferencesAvailable(list);
        }
    }

    @UiThread
    void notifyConferencesListenerError(
            WeakReference<IConferencesListener> listener) {
        isDownloadingAllConferencesData = false;
        final IConferencesListener internalListener = listener.get();
        if (internalListener != null) {
            internalListener.onConferencesError();
        }
    }

    public void openLastConference() {
        notifyConferenceListenerStart(confDataListener);
        notifyConferenceListenerSuccess(confDataListener, true);
    }

    public boolean isLastSelectedConference(ConferenceApiModel selectedConference) {
        final Optional<String> id = getActiveConferenceId();
        return id.isPresent() && id.get().equalsIgnoreCase(selectedConference.id);
    }

    public void requestConferenceChange() {
        settings.edit().requestedConferenceChange().put(true).apply();
    }

    public boolean requestedChangeConference() {
        final boolean result = settings.requestedConferenceChange().get();
        settings.edit().requestedConferenceChange().put(false).apply();
        return result;
    }

    public boolean isConferenceChoosen() {
        return getActiveConference().isPresent();
    }

    private boolean isDownloadingAllConferencesData = false;

    private WeakReference<IConferencesListener> allConferencesDataListener;

    public boolean registerAllConferencesDataListener(IConferencesListener listener) {
        allConferencesDataListener = new WeakReference<>(listener);
        return isDownloadingAllConferencesData;
    }

    public void unregisterAllConferencesDataListener() {
        allConferencesDataListener.clear();
    }

    private boolean isDownloadingConferenceData = false;

    private WeakReference<IConferenceDataListener> confDataListener;

    public boolean registerConferenceDataListener(IConferenceDataListener listener) {
        confDataListener = new WeakReference<>(listener);
        return isDownloadingConferenceData;
    }

    public void unregisterConferenceDataListener() {
        confDataListener.clear();
    }

    public void initWitStaticData() {
        conferenceDownloader.initWitStaticData();
    }

    @Background
    public void updateSlotsIfNeededInBackground() {
        final Optional<RealmConference> conference = getActiveConference();
        if (conference.isPresent()) {
            final String confCode = conference.get().getId();
            slotsDataManager.updateSlotsIfNeededInBackground(confCode);
        }
    }

    @Background
    public void fetchConferenceData(
            ConferenceApiModel conferenceApiModel) {
        final String confCode = conferenceApiModel.id;
        try {
            notifyConferenceListenerStart(confDataListener);

            tracksDownloader.downloadTracksDescriptions(confCode);
            final boolean isAnyTalks = slotsDataManager.fetchTalksSync(confCode);
            speakersDataManager.fetchSpeakersSync(confCode);

            saveActiveConference(conferenceApiModel);
            final List<ConferenceDay> conferenceDays = getConferenceDays();
            scheduleFilterManager.createDayFiltersDefinition(conferenceDays);

            notifyConferenceListenerSuccess(confDataListener, isAnyTalks);
        } catch (IOException e) {
            clearCurrentConferenceData();
            notifyConferenceListenerError(confDataListener);
        }
    }

    @UiThread
    void notifyConferenceListenerStart(WeakReference<IConferenceDataListener> listener) {
        isDownloadingConferenceData = true;
        final IConferenceDataListener internalListener = listener.get();
        if (internalListener != null) {
            internalListener.onConferenceDataStart();
        }
    }

    @UiThread
    void notifyConferenceListenerSuccess(WeakReference<IConferenceDataListener> listener, boolean isAnyTalks) {
        isDownloadingConferenceData = false;
        final IConferenceDataListener internalListener = listener.get();
        if (internalListener != null) {
            internalListener.onConferenceDataAvailable(isAnyTalks);
        }
    }

    @UiThread
    void notifyConferenceListenerError(WeakReference<IConferenceDataListener> listener) {
        isDownloadingConferenceData = false;
        final IConferenceDataListener internalListener = listener.get();
        if (internalListener != null) {
            internalListener.onConferenceDataError();
        }
    }

    public List<ConferenceDay> getConferenceDays() {
//        final Optional<RealmConference> realmConference = getActiveConference();
//        if (realmConference.isPresent()) {
//
//        }

        // TODO Take dates from model!
        final String fromDate = "2015-11-09T01:00:00.000Z";
        final String toDate = "2015-11-13T23:00:00.000Z";
//        final String fromDate = realmConference.getFromDate();
//        final String toDate = realmConference.getToDate();
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

    public Optional<RealmConference> getActiveConference() {
        final Realm realm = realmProvider.getRealm();
        return Optional.ofNullable(realm.where(RealmConference.class).findFirst());
    }

    public Optional<String> getActiveConferenceId() {
        final RealmConference conference = getActiveConference().orElse(null);
        return Optional.ofNullable(conference != null ? conference.getId() : null);
    }

    public void clearCurrentConferenceData() {
        Logger.l("clearCurrentConferenceData");

        clearCurrentConference();
        clearSlotsData();
        clearTracksData();
        clearSpeakersData();
        clearFiltersDefinitions();
        clearCache();
        userManager.clearCode();
    }

    public Optional<ConferenceDay> getCurrentConfDay() {
        return Stream.of(conferenceDays).filter(ConferenceDay::isRunning).findFirst();
    }

    public long getNow() {
        //TODO Tests!
        final String test = "2015-11-10T10:00:00.000Z";

        final DateTime now = new DateTime();
        final DateTime result = parseConfDate(test).withMinuteOfHour(now.getMinuteOfHour())
                .withHourOfDay(now.getHourOfDay());

        return result.getMillis();
//        return System.currentTimeMillis();
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

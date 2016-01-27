package com.devoxx.data.conference;

import com.devoxx.connection.cfp.model.ConferenceApiModel;
import com.devoxx.data.conference.model.ConferenceDay;
import com.devoxx.data.downloader.ConferenceDownloader;
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

@EBean(scope = EBean.Scope.Singleton)
public class ConferenceManager {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public interface IOnConferencesAvailableListener {
        void onConferencesAvailable(List<ConferenceApiModel> conferenceS);

        void onConferencesError();
    }

    @Bean
    ScheduleFilterManager scheduleFilterManager;

    @Bean
    ConferenceDownloader conferenceDownloader;

    @Background
    public void fetchAvailableConferences(IOnConferencesAvailableListener listener) {
        try {
            final List<ConferenceApiModel> conferences = conferenceDownloader.fetchAllConferences();
            notifyConferencesAvailableListenerAboutSuccess(listener, conferences);
        } catch (IOException e) {
            notifyConferencesAvailableListenerAboutError(listener);
        }
    }

    @UiThread
    void notifyConferencesAvailableListenerAboutSuccess(
            IOnConferencesAvailableListener listener, List<ConferenceApiModel> list) {
        listener.onConferencesAvailable(list);
    }

    @UiThread
    void notifyConferencesAvailableListenerAboutError(
            IOnConferencesAvailableListener listener) {
        listener.onConferencesError();
    }

    public void fetchConferenceData(ConferenceApiModel conferenceApiModel) {
        // TODO Download speakers.
        // TODO Download talks.
        // TODO Download tracks.
        final List<ConferenceDay> conferenceDays = getConferenceDays();
        scheduleFilterManager.createDayFiltersDefinition(conferenceDays);
    }

    public List<ConferenceDay> getConferenceDays() {
        // TODO Test values.
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

    private DateTime convertStringDate(String stringDate) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_FORMAT);
        return formatter.parseDateTime(stringDate);
    }
}

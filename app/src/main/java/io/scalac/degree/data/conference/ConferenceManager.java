package io.scalac.degree.data.conference;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.scalac.degree.data.conference.model.ConferenceDay;
import io.scalac.degree.data.schedule.filter.ScheduleFilterManager;

@EBean
public class ConferenceManager {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @Bean
    ScheduleFilterManager scheduleFilterManager;

    public void fetchAvailableConferences() {
        // TODO TBD, download cfp.json from the server!
    }

    public void fetchConferenceData(String confCode) {
        // TODO TBD
        final List<ConferenceDay> conferenceDays = getConferenceDays();
        scheduleFilterManager.createDayFiltersDefinition(conferenceDays);
    }

    public List<ConferenceDay> getConferenceDays() {
        // TODO Test values.
        final String fromDate = "2015-11-09T01:00:00.000Z";
        final String toDate = "2015-11-13T23:00:00.000Z";
        final DateTime fromConfDate = convertStringDate(fromDate);
        final DateTime toConfDate = convertStringDate(toDate);

        final int daysSpan = Days.daysBetween(
                fromConfDate.toLocalDate(), toConfDate.toLocalDate()).getDays();

        final List<ConferenceDay> result = new ArrayList<>(daysSpan);
        for (int i = 0; i < daysSpan; i++) {
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

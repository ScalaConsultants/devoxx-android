package io.scalac.degree.android.view.listholder.schedule;

import io.scalac.degree.android.view.list.schedule.TimespanItemView;

public class TimespanItemHolder extends BaseItemHolder {

    private TimespanItemView timespanItemView;

    public TimespanItemHolder(TimespanItemView aTimespanItemView) {
        super(aTimespanItemView);
        timespanItemView = aTimespanItemView;
    }

    public void setupTimespan(long timeStart, long timeEnd) {
        timespanItemView.setupTimespan(timeStart, timeEnd);
    }
}

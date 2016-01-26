package com.devoxx.android.view.listholder.schedule;

import com.devoxx.android.view.list.schedule.TimespanItemView;

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

package io.scalac.degree.android.view.listholder.schedule;

import io.scalac.degree.android.view.list.schedule.TimespanItemView;
import io.scalac.degree.connection.model.BreakApiModel;
import io.scalac.degree.connection.model.SlotApiModel;

public class TimespanItemHolder extends BaseItemHolder {

    private TimespanItemView timespanItemView;

    public TimespanItemHolder(TimespanItemView aTimespanItemView) {
        super(aTimespanItemView);
        timespanItemView = aTimespanItemView;
    }

    public void setupBreak(SlotApiModel breakModel) {
        timespanItemView.setupBreak(breakModel);
    }
}

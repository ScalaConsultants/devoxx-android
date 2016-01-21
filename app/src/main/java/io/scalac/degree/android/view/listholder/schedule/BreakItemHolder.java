package io.scalac.degree.android.view.listholder.schedule;

import android.view.View;

import io.scalac.degree.android.view.list.schedule.BreakItemView;
import io.scalac.degree.connection.model.BreakApiModel;
import io.scalac.degree.connection.model.SlotApiModel;

public class BreakItemHolder extends BaseItemHolder {

    private BreakItemView breakItemView;

    public BreakItemHolder(BreakItemView aBreakItemView) {
        super(aBreakItemView);
        breakItemView = aBreakItemView;
    }

    public void setupBreak(SlotApiModel breakModel) {
        breakItemView.setupBreak(breakModel);
    }
}

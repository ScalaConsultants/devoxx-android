package com.devoxx.android.view.listholder.schedule;

import com.devoxx.android.view.list.schedule.BreakItemView;
import com.devoxx.connection.model.SlotApiModel;

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

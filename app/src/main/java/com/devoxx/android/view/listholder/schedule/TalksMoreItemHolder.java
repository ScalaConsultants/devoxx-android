package com.devoxx.android.view.listholder.schedule;

import com.devoxx.android.adapter.schedule.model.TalksScheduleItem;
import com.devoxx.android.view.list.schedule.TalksMoreItemView;

public class TalksMoreItemHolder extends BaseItemHolder {

    private TalksMoreItemView talkItemView;

    public TalksMoreItemHolder(TalksMoreItemView itemView) {
        super(itemView);
        talkItemView = itemView;
    }

    public void setupMore(TalksScheduleItem talksScheduleItem, Runnable onOpenMoreAction) {
        talkItemView.setupMore(talksScheduleItem, onOpenMoreAction);
    }

    public void setRunIndicatorVisibility(TalksScheduleItem item) {
        talkItemView.setRunIndicatorVisibility(item);
    }

    public void toggleIndicator() {
        talkItemView.toggleIndicator();
    }
}

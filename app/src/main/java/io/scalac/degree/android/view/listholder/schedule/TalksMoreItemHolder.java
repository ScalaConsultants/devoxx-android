package io.scalac.degree.android.view.listholder.schedule;

import io.scalac.degree.android.adapter.schedule.model.TalksScheduleItem;
import io.scalac.degree.android.view.list.schedule.TalksMoreItemView;

public class TalksMoreItemHolder extends BaseItemHolder {

    private TalksMoreItemView talkItemView;

    public TalksMoreItemHolder(TalksMoreItemView itemView) {
        super(itemView);
        talkItemView = itemView;
    }

    public void setupMore(TalksScheduleItem talksScheduleItem, Runnable onOpenMoreAction) {
        talkItemView.setupMore(talksScheduleItem, onOpenMoreAction);
    }

    public void toggleIndicator() {
        talkItemView.toggleIndicator();
    }
}

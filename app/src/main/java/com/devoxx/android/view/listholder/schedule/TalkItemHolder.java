package com.devoxx.android.view.listholder.schedule;

import com.devoxx.android.view.list.schedule.TalkItemView;
import com.devoxx.connection.model.SlotApiModel;

public class TalkItemHolder extends BaseItemHolder {

    private TalkItemView talkItemView;

    public TalkItemHolder(TalkItemView itemView) {
        super(itemView);
        talkItemView = itemView;
    }

    public void setupTalk(SlotApiModel slotApiModel, boolean withRunningIndicator) {
        talkItemView.setupTalk(slotApiModel);
        talkItemView.showRunningIndicator(withRunningIndicator);
    }
}

package io.scalac.degree.android.view.listholder.schedule;

import io.scalac.degree.android.view.list.schedule.TalkItemView;
import io.scalac.degree.connection.model.SlotApiModel;

public class TalkItemHolder extends BaseItemHolder {

    private TalkItemView talkItemView;

    public TalkItemHolder(TalkItemView itemView) {
        super(itemView);
        talkItemView = itemView;
    }

    public void setupTalk(SlotApiModel slotApiModel) {
        talkItemView.setupTalk(slotApiModel);
    }
}

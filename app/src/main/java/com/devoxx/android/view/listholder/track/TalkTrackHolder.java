package com.devoxx.android.view.listholder.track;

import com.devoxx.android.view.TrackTalkItemView;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.data.downloader.TracksDownloader;

public class TalkTrackHolder extends BaseTrackHolder {

    private TrackTalkItemView trackTalkItemView;
    private TracksDownloader tracksDownloader;

    public TalkTrackHolder(TrackTalkItemView itemView, TracksDownloader aTracksDownloader) {
        super(itemView);
        trackTalkItemView = itemView;
        tracksDownloader = aTracksDownloader;
    }

    @Override
    public void setupView(SlotApiModel slotModel) {
        trackTalkItemView.setTitle(slotModel.talk.title);
        trackTalkItemView.setIcon(obtainTrackIconUrl(slotModel));
        trackTalkItemView.setSpeakers(slotModel.talk.getReadableSpeakers());
        trackTalkItemView.setTime(slotModel.fromTime);
    }

    private String obtainTrackIconUrl(SlotApiModel slotModel) {
        return tracksDownloader.getTrackIconUrl(slotModel.talk.track);
    }
}

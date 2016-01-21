package io.scalac.degree.android.view.listholder.track;

import io.scalac.degree.android.view.TrackTalkItemView;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.downloader.TracksDownloader;

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

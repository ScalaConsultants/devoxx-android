package io.scalac.degree.android.adapter;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.scalac.degree.android.view.TrackTalkItemView;
import io.scalac.degree.android.view.TrackTalkItemView_;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.downloader.TracksDownloader;

@EBean
public class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.BaseHolder> {

    @Bean
    TracksDownloader tracksDownloader;

    private List<SlotApiModel> data = new ArrayList<>();

    public void setData(List<SlotApiModel> aData) {
        data.clear();
        data.addAll(aData);
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TalkHolder(TrackTalkItemView_.build(parent.getContext()), tracksDownloader);
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        holder.setupView(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public SlotApiModel getClickedItem(int position) {
        return data.get(position);
    }

    static class BaseHolder extends RecyclerView.ViewHolder {

        public BaseHolder(View itemView) {
            super(itemView);
        }

        public void setupView(SlotApiModel slotApiModel) {
            // Nothing here.
        }
    }

    static class TalkHolder extends BaseHolder {

        private TrackTalkItemView trackTalkItemView;
        private TracksDownloader tracksDownloader;

        public TalkHolder(TrackTalkItemView itemView, TracksDownloader tracksDownloader) {
            super(itemView);
            trackTalkItemView = itemView;
            this.tracksDownloader = tracksDownloader;
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
}

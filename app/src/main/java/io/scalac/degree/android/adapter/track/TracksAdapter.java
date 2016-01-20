package io.scalac.degree.android.adapter.track;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.scalac.degree.android.view.TrackTalkItemView_;
import io.scalac.degree.android.view.listholder.BaseTrackHolder;
import io.scalac.degree.android.view.listholder.TalkTrackHolder;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.downloader.TracksDownloader;

@EBean
public class TracksAdapter extends RecyclerView.Adapter<BaseTrackHolder> {

    @Bean
    TracksDownloader tracksDownloader;

    private final List<SlotApiModel> data = new ArrayList<>();

    public void setData(List<SlotApiModel> aData) {
        data.clear();
        data.addAll(aData);
    }

    @Override
    public BaseTrackHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TalkTrackHolder(TrackTalkItemView_.build(parent.getContext()), tracksDownloader);
    }

    @Override
    public void onBindViewHolder(BaseTrackHolder holder, int position) {
        holder.setupView(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public SlotApiModel getClickedItem(int position) {
        return data.get(position);
    }
}

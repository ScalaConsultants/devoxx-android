package com.devoxx.android.adapter.track;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.devoxx.android.view.list.schedule.TalkItemView_;
import com.devoxx.android.view.listholder.track.BaseTrackHolder;
import com.devoxx.android.view.listholder.track.TalkTrackHolder;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.data.downloader.TracksDownloader;
import com.devoxx.data.manager.NotificationsManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

@EBean
public class TracksAdapter extends RecyclerView.Adapter<BaseTrackHolder> {

    @Bean
    TracksDownloader tracksDownloader;

    @Bean
    NotificationsManager notificationsManager;

    private final List<SlotApiModel> data = new ArrayList<>();

    public void setData(List<SlotApiModel> aData) {
        data.clear();
        data.addAll(aData);
    }

    @Override
    public BaseTrackHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TalkTrackHolder(TalkItemView_.build(parent.getContext()));
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

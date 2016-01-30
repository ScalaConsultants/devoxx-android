package com.devoxx.android.adapter.track;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.devoxx.android.view.list.schedule.TalkItemView_;
import com.devoxx.android.view.listholder.track.BaseTrackHolder;
import com.devoxx.android.view.listholder.track.TalkTrackHolder;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.data.conference.ConferenceManager;
import com.devoxx.data.conference.model.ConferenceDay;
import com.devoxx.data.downloader.TracksDownloader;
import com.devoxx.data.manager.NotificationsManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

@EBean
public class TracksAdapter extends RecyclerView.Adapter<BaseTrackHolder> {

    public static final int INVALID_RUNNING_FIRST_INDEX = -1;

    @Bean
    TracksDownloader tracksDownloader;

    @Bean
    ConferenceManager conferenceManager;

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
        final SlotApiModel slot = data.get(position);
        holder.setupView(slot, isRunningItem(slot));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public SlotApiModel getClickedItem(int position) {
        return data.get(position);
    }

    public int getRunningFirstIndex() {
        int index = INVALID_RUNNING_FIRST_INDEX;
        final int size = data.size();
        for (int i = 0; i < size; i++) {
            if (isRunningItem(data.get(i))) {
                index = i;
                break;
            }
        }
        return index;
    }

    private boolean isRunningItem(SlotApiModel slot) {
        final long currentTime = conferenceManager.getNow();
        return slot.fromTimeMillis <= currentTime
                && slot.toTimeMillis >= currentTime;
    }
}

package io.scalac.degree.android.adapter.schedule;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.lucasr.twowayview.ItemClickSupport;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import io.scalac.degree.android.adapter.schedule.model.BreakScheduleItem;
import io.scalac.degree.android.adapter.schedule.model.ScheduleItem;
import io.scalac.degree.android.adapter.schedule.model.TalksScheduleItem;
import io.scalac.degree.android.adapter.schedule.model.creator.ScheduleLineupDataCreator;
import io.scalac.degree.android.view.list.schedule.BreakItemView_;
import io.scalac.degree.android.view.list.schedule.TalkItemView_;
import io.scalac.degree.android.view.list.schedule.TalksMoreItemView_;
import io.scalac.degree.android.view.list.schedule.TimespanItemView_;
import io.scalac.degree.android.view.listholder.schedule.BaseItemHolder;
import io.scalac.degree.android.view.listholder.schedule.BreakItemHolder;
import io.scalac.degree.android.view.listholder.schedule.TalkItemHolder;
import io.scalac.degree.android.view.listholder.schedule.TalksMoreItemHolder;
import io.scalac.degree.android.view.listholder.schedule.TimespanItemHolder;
import io.scalac.degree.connection.model.SlotApiModel;

@EBean
public class ScheduleDayLineupAdapter extends RecyclerView.Adapter<BaseItemHolder> {

    public static final int TIMESPAN_VIEW = 1;
    public static final int BREAK_VIEW = 2;
    public static final int TALK_VIEW = 3;
    public static final int TALK_MORE_VIEW = 4;

    @IntDef({
            TIMESPAN_VIEW,
            BREAK_VIEW,
            TALK_VIEW,
            TALK_MORE_VIEW
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewType {
    }

    @Bean
    ScheduleLineupDataCreator scheduleLineupDataCreator;

    private final List<ScheduleItem> data = new ArrayList<>();
    private ItemClickSupport.OnItemClickListener clickListener;

    public void setData(List<ScheduleItem> aData) {
        data.clear();
        data.addAll(aData);
    }

    public void setListener(ItemClickSupport.OnItemClickListener listener) {
        clickListener = listener;
    }

    public SlotApiModel getClickedSlot(int position) {
        return getItem(position).getItem(position);
    }

    @Override
    public BaseItemHolder onCreateViewHolder(
            ViewGroup parent, @ScheduleDayLineupAdapter.ViewType int viewType) {
        final Context context = parent.getContext();
        final BaseItemHolder result;

        switch (viewType) {
            case TIMESPAN_VIEW:
                result = new TimespanItemHolder(TimespanItemView_.build(context));
                break;
            case BREAK_VIEW:
                result = new BreakItemHolder(BreakItemView_.build(context));
                break;
            case TALK_VIEW:
                result = new TalkItemHolder(TalkItemView_.build(context));
                break;
            case TALK_MORE_VIEW:
                result = new TalksMoreItemHolder(TalksMoreItemView_.build(context));
                break;
            default:
                throw new IllegalStateException("No holder for view type: " + viewType);
        }

        return result;
    }

    @Override
    public void onBindViewHolder(BaseItemHolder holder, int position) {
        if (holder instanceof BreakItemHolder) {
            setupBreakItemHolder((BreakItemHolder) holder, getItem(position));
        } else if (holder instanceof TalkItemHolder) {
            setupTalkItemHolder(holder, getItem(position), position);
        } else if (holder instanceof TimespanItemHolder) {
            setupTimespanItemHolder((TimespanItemHolder) holder, getItem(position));
        } else if (holder instanceof TalksMoreItemHolder) {
            setupMoreItemHolder((TalksMoreItemHolder) holder, position);
        }
    }

    private void setupMoreItemHolder(TalksMoreItemHolder holder, int position) {
        final TalksScheduleItem item = (TalksScheduleItem) getItem(position);
        holder.setupMore(item, () -> {
            item.switchTalksVisibility();
            holder.toggleIndicator();
            scheduleLineupDataCreator.refreshIndexes(data);
            notifyItemRangeRemoved(item.getStartIndexForHide(position),
                    item.getEndIndexForHide(position));
        });
    }

    private void setupTimespanItemHolder(TimespanItemHolder holder, ScheduleItem scheduleItem) {
        final TalksScheduleItem item = (TalksScheduleItem) scheduleItem;
        holder.setupTimespan(item.getStartTime(), item.getEndTime());
    }

    private void setupBreakItemHolder(BreakItemHolder holder, ScheduleItem scheduleItem) {
        final BreakScheduleItem breakScheduleItem = (BreakScheduleItem) scheduleItem;
        final SlotApiModel breakModel = breakScheduleItem.getBreakModel();
        holder.setupBreak(breakModel);
    }

    private void setupTalkItemHolder(BaseItemHolder holder, ScheduleItem scheduleItem, int position) {
        final TalksScheduleItem talksScheduleItem = (TalksScheduleItem) scheduleItem;
        final SlotApiModel slotModel = talksScheduleItem.getItem(position);
        ((TalkItemHolder) holder).setupTalk(slotModel);
        setupOnItemClickListener(holder, position);
    }

    private void setupOnItemClickListener(BaseItemHolder holder, int position) {
        holder.itemView.setOnClickListener(v ->
                clickListener.onItemClick(null, v, position, getItemId(position)));
    }

    @Override
    @ScheduleDayLineupAdapter.ViewType
    public int getItemViewType(int position) {
        return getItem(position).getItemType(position);
    }

    @Override
    public int getItemCount() {
        int result = 0;
        for (ScheduleItem scheduleItem : data) {
            result += scheduleItem.getSize();
        }
        return result;
    }

    private ScheduleItem getItem(int position) {
        for (ScheduleItem scheduleItem : data) {
            final int startIndex = scheduleItem.getStartIndex();
            final int stopIndex = scheduleItem.getStopIndex();
            if (position >= startIndex && position <= stopIndex) {
                return scheduleItem;
            }
        }
        throw new IllegalStateException("No item for position: " + position);
    }
}

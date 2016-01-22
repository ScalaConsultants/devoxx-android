package io.scalac.degree.android.adapter.schedule;

import org.androidannotations.annotations.EBean;

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
import io.scalac.degree.android.view.list.schedule.BreakItemView_;
import io.scalac.degree.android.view.list.schedule.TalkItemView_;
import io.scalac.degree.android.view.list.schedule.TimespanItemView_;
import io.scalac.degree.android.view.listholder.schedule.BaseItemHolder;
import io.scalac.degree.android.view.listholder.schedule.BreakItemHolder;
import io.scalac.degree.android.view.listholder.schedule.TalkItemHolder;
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

    private final List<ScheduleItem> data = new ArrayList<>();

    public void setData(List<ScheduleItem> aData) {
        data.clear();
        data.addAll(aData);
    }

    public SlotApiModel getClickedSlot(int position) {
        return getItem(position).getItem(position);
    }

    @Override
    public BaseItemHolder onCreateViewHolder(
            ViewGroup parent, @ScheduleDayLineupAdapter.ViewType int viewType) {
        final Context context = parent.getContext();
        BaseItemHolder result = null;

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
                break;
            default:
                throw new IllegalStateException("No holder for view type: " + viewType);
        }

        return result;
    }

    @Override
    public void onBindViewHolder(BaseItemHolder holder, int position) {
        if (holder instanceof BreakItemHolder) {
            final BreakScheduleItem breakScheduleItem = (BreakScheduleItem) getItem(position);
            final SlotApiModel breakModel = breakScheduleItem.getBreakModel();
            ((BreakItemHolder) holder).setupBreak(breakModel);
        } else if (holder instanceof TalkItemHolder) {
            final TalksScheduleItem talksScheduleItem = (TalksScheduleItem) getItem(position);
            final SlotApiModel breakModel = talksScheduleItem.getSlotModel(position);
            ((TalkItemHolder) holder).setupTalk(breakModel);
        } else if (holder instanceof TimespanItemHolder) {
            final TalksScheduleItem item = (TalksScheduleItem) getItem(position);
            ((TimespanItemHolder) holder).setupTimespan(item.getStartTime(), item.getEndTime());
        }
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
        ScheduleItem result = null;
        for (ScheduleItem scheduleItem : data) {
            final int startIndex = scheduleItem.getStartIndex();
            final int stopIndex = scheduleItem.getStopIndex();
            if (position >= startIndex && position <= stopIndex) {
                result = scheduleItem;
                break;
            }
        }
        return result;
    }
}
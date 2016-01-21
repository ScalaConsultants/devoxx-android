package io.scalac.degree.android.adapter.schedule.model;

import io.scalac.degree.android.adapter.schedule.ScheduleDayLineupAdapter;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.utils.tuple.Tuple;

public abstract class ScheduleItem {

    private final Tuple<Long, Long> slotTimespan;
    private final int startIndex, stopIndex;

    public ScheduleItem(long startTime, long endTime, int aStartIndex, int aStopIndex) {
        startIndex = aStartIndex;
        this.stopIndex = aStopIndex;
        slotTimespan = new Tuple<>(startTime, endTime);
    }

    public long getStartTime() {
        return slotTimespan.first;
    }

    public long getEndTime() {
        return slotTimespan.second;
    }

    public int getStopIndex() {
        return stopIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public abstract int getSize();

    @ScheduleDayLineupAdapter.ViewType
    public abstract int getItemType(int position);

    public abstract SlotApiModel getItem(int position);
}

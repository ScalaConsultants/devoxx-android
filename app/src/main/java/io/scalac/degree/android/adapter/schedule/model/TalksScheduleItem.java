package io.scalac.degree.android.adapter.schedule.model;

import java.util.ArrayList;
import java.util.List;

import io.scalac.degree.android.adapter.schedule.ScheduleDayLineupAdapter;
import io.scalac.degree.connection.model.SlotApiModel;

public class TalksScheduleItem extends ScheduleItem {

    private static final int EXTRA_TIMESPAN_ELEMENT_COUNT = 1;
    private static final int TIMESPAN_INDEX = 0;

    private List<SlotApiModel> favouredSlots;
    private List<SlotApiModel> otherSlots;

    public TalksScheduleItem(long startTime, long endTime, int strIndex, int stpIndex) {
        super(startTime, endTime, strIndex, stpIndex);
        favouredSlots = new ArrayList<>();
        otherSlots = new ArrayList<>();
    }

    public void setFavouredSlots(List<SlotApiModel> slots) {
        favouredSlots = new ArrayList<>(slots.size());
        favouredSlots.addAll(slots);
    }

    public void setOtherSlots(List<SlotApiModel> slots) {
        otherSlots = new ArrayList<>(slots.size());
        otherSlots.addAll(slots);
    }

    @Override
    public int getSize() {
        return favouredSlots.size() + otherSlots.size() + EXTRA_TIMESPAN_ELEMENT_COUNT;
    }

    @Override
    @ScheduleDayLineupAdapter.ViewType
    public int getItemType(int position) {

        // TODO Handle breaks and favs!

        final int probableTimespanIndex = position - getStartIndex();
        if (probableTimespanIndex == TIMESPAN_INDEX) {
            return ScheduleDayLineupAdapter.TIMESPAN_VIEW;
        } else {
            return ScheduleDayLineupAdapter.TALK_VIEW;
        }
    }

    @Override
    public SlotApiModel getItem(int position) {
        // TODO Handle breaks and favs!

        final int localIndex = position - getStartIndex();
        return otherSlots.get(localIndex - 1);
    }

    public SlotApiModel getSlotModel(int position) {
        // TODO Handle breaks and favs!

        if (getItemType(position) == ScheduleDayLineupAdapter.TALK_VIEW) {
            final int localIndex = position - getStartIndex();
            final int slotIndex = localIndex - EXTRA_TIMESPAN_ELEMENT_COUNT;
            return otherSlots.get(slotIndex);
        } else if (getItemType(position) == ScheduleDayLineupAdapter.TIMESPAN_VIEW) {
            return otherSlots.get(0);
        } else {
            throw new IllegalArgumentException("Bad position!");
        }
    }
}

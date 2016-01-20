package io.scalac.degree.android.adapter.schedule.model;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;

import java.util.ArrayList;
import java.util.List;

import io.scalac.degree.android.adapter.schedule.ScheduleDayLineupAdapter;
import io.scalac.degree.connection.model.SlotApiModel;

public class TalksScheduleItem extends ScheduleItem {

    private List<ScheduleSlotWrapper> favouredSlots;
    private List<ScheduleSlotWrapper> otherSlots;

    public TalksScheduleItem(long startTime, long endTime, int aStartIndex, int aStopIndex) {
        super(startTime, endTime, aStartIndex, aStopIndex);
        favouredSlots = new ArrayList<>();
        otherSlots = new ArrayList<>();
    }

    public void setFavouredSlots(List<SlotApiModel> slots) {
        favouredSlots = new ArrayList<>(slots.size());
        for (SlotApiModel slot : slots) {
            favouredSlots.add(new ScheduleSlotWrapper(slot));
        }
    }

    public void setOtherSlots(List<SlotApiModel> slots) {
        otherSlots = new ArrayList<>(slots.size());
        for (SlotApiModel slot : slots) {
            otherSlots.add(new ScheduleSlotWrapper(slot));
        }
    }

    @Override
    public boolean isVisible() {
        final boolean isAnyFavsVisible = Stream.of(favouredSlots)
                .allMatch(new Predicate<ScheduleSlotWrapper>() {
                    @Override
                    public boolean test(ScheduleSlotWrapper value) {
                        return value.isVisible();
                    }
                });

        final boolean isAnyOtherVisible = Stream.of(otherSlots)
                .allMatch(new Predicate<ScheduleSlotWrapper>() {
                    @Override
                    public boolean test(ScheduleSlotWrapper value) {
                        return value.isVisible();
                    }
                });

        return isAnyFavsVisible && isAnyOtherVisible;
    }

    @Override
    public int getSize() {
        return favouredSlots.size() + otherSlots.size();
    }

    @Override
    @ScheduleDayLineupAdapter.ViewType
    public int getItemType(int position) {

        // TODO Handle breaks and favs!

        final int localIndex = position - getStartIndex();
        if (localIndex == 0) {
            return ScheduleDayLineupAdapter.TIMESPAN_VIEW;
        } else {
            return ScheduleDayLineupAdapter.TALK_VIEW;
        }
    }

    public SlotApiModel getSlotModel(int position) {
        // TODO Handle breaks and favs!

        if (getItemType(position) == ScheduleDayLineupAdapter.TALK_VIEW) {
            final int localIndex = position - getStartIndex();
            return otherSlots.get(localIndex - 1).getSlotApiModel();
        } else if (getItemType(position) == ScheduleDayLineupAdapter.TIMESPAN_VIEW) {
            return otherSlots.get(0).getSlotApiModel();
        } else {
            throw new IllegalArgumentException("Bad position!");
        }
    }
}

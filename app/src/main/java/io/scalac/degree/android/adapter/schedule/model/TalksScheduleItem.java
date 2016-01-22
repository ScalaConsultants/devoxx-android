package io.scalac.degree.android.adapter.schedule.model;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import io.scalac.degree.android.adapter.schedule.ScheduleDayLineupAdapter;
import io.scalac.degree.connection.model.SlotApiModel;

public class TalksScheduleItem extends ScheduleItem {

    private static final int EXTRA_TIMESPAN_ELEMENT_COUNT = 1;
    private static final int EXTRA_OPEN_MORE_ELEMENT_COUNT = 1;

    private static final int TIMESPAN_INDEX = 0;

    private List<SlotApiModel> favouredSlots;
    private List<SlotApiModel> otherSlots;
    private boolean isOthersVisible = true;

    public TalksScheduleItem(long startTime, long endTime, int strIndex, int stpIndex) {
        super(startTime, endTime, strIndex, stpIndex);
        favouredSlots = new ArrayList<>();
        otherSlots = new ArrayList<>();
    }

    public void addFavouredSlot(SlotApiModel model) {
        favouredSlots.add(model);
    }

    public void addOtherSlot(SlotApiModel model) {
        otherSlots.add(model);
    }

    public int talksCount() {
        return countTalks(otherSlots) + countTalks(favouredSlots);
    }

    public int tracksCount() {
        return countTracks(otherSlots) + countTracks(favouredSlots);
    }

    public boolean isOthersVisible() {
        return isOthersVisible;
    }

    @Override
    public int getSize() {
        final int result;
        if (isOthersVisible) {
            result = favouredSlots.size() + otherSlots.size();
        } else {
            result = favouredSlots.size();
        }
        return result + EXTRA_TIMESPAN_ELEMENT_COUNT
                + EXTRA_OPEN_MORE_ELEMENT_COUNT;
    }

    @Override
    @ScheduleDayLineupAdapter.ViewType
    public int getItemType(int position) {
        final int calendarOpenMoreIndexWithNoFavs = TIMESPAN_INDEX
                + EXTRA_TIMESPAN_ELEMENT_COUNT;
        final int calendarOpenMoreIndexWithFavs = TIMESPAN_INDEX + favouredSlots.size()
                + EXTRA_OPEN_MORE_ELEMENT_COUNT;

        final int innerIndex = position - getStartIndex();

        if (innerIndex == TIMESPAN_INDEX) {
            return ScheduleDayLineupAdapter.TIMESPAN_VIEW;
        }

        final int probableMoreIndex = favouredSlots.isEmpty() ?
                calendarOpenMoreIndexWithNoFavs : calendarOpenMoreIndexWithFavs;

        return (innerIndex == probableMoreIndex) ?
                ScheduleDayLineupAdapter.TALK_MORE_VIEW : ScheduleDayLineupAdapter.TALK_VIEW;
    }

    @Override
    public SlotApiModel getItem(int globalPosition) {
        final int localIndex = globalPosition - getStartIndex();
        if (favouredSlots.isEmpty()) {
            return getSlotWithoutFavs(globalPosition, localIndex);
        } else {
            return getSlotWithFavs(localIndex);
        }
    }

    public void switchTalksVisibility() {
        isOthersVisible ^= true;
    }

    public int getStartIndexForHide(int globalIndex) {
        return globalIndex + EXTRA_TIMESPAN_ELEMENT_COUNT +
                favouredSlots.size() + EXTRA_OPEN_MORE_ELEMENT_COUNT;
    }

    public int getEndIndexForHide(int globalIndex) {
        return getStartIndexForHide(globalIndex) + otherSlots.size();
    }

    private SlotApiModel getSlotWithFavs(int localIndex) {
        final int favsStartIndex = TIMESPAN_INDEX + EXTRA_TIMESPAN_ELEMENT_COUNT;
        final int favsEndIndex = favsStartIndex + favouredSlots.size() - 1;

        if (localIndex >= favsStartIndex && localIndex <= favsEndIndex) {
            return favouredSlots.get(localIndex - favsStartIndex);
        } else {
            final int slotIndex = localIndex - (favsEndIndex +
                    EXTRA_OPEN_MORE_ELEMENT_COUNT + EXTRA_TIMESPAN_ELEMENT_COUNT);
            return otherSlots.get(slotIndex);
        }
    }

    private SlotApiModel getSlotWithoutFavs(int globalPosition, int localIndex) {
        if (getItemType(globalPosition) == ScheduleDayLineupAdapter.TALK_VIEW) {
            final int slotIndex = localIndex - EXTRA_TIMESPAN_ELEMENT_COUNT
                    - EXTRA_OPEN_MORE_ELEMENT_COUNT;
            return otherSlots.get(slotIndex);
        } else if (getItemType(globalPosition) == ScheduleDayLineupAdapter.TIMESPAN_VIEW) {
            return otherSlots.get(0); // We need to get only start-end time from slot.
        } else {
            throw new IllegalArgumentException("Bad globalPosition!");
        }
    }

    private int countTalks(List<SlotApiModel> slotApiModels) {
        return Stream.of(slotApiModels)
                .filter(SlotApiModel::isTalk)
                .collect(Collectors.counting()).intValue();
    }

    private int countTracks(List<SlotApiModel> slotApiModels) {
        return Stream.of(slotApiModels)
                .filter(SlotApiModel::isTalk)
                .groupBy(value -> value.talk.track)
                .collect(Collectors.counting()).intValue();
    }
}

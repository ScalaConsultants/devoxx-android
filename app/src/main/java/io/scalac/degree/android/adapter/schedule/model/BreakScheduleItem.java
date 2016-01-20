package io.scalac.degree.android.adapter.schedule.model;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;

import java.util.ArrayList;
import java.util.List;

import io.scalac.degree.android.adapter.schedule.ScheduleDayLineupAdapter;
import io.scalac.degree.connection.model.SlotApiModel;

public class BreakScheduleItem extends ScheduleItem {

    private List<ScheduleSlotWrapper> scheduleSlotWrappers;

    public BreakScheduleItem(
            long startTime,
            long endTime,
            int aStartIndex,
            int aStopIndex,
            List<SlotApiModel> aBreakModels) {
        super(startTime, endTime, aStartIndex, aStopIndex);

        scheduleSlotWrappers = new ArrayList<>(aBreakModels.size());
        for (SlotApiModel aBreakModel : aBreakModels) {
            scheduleSlotWrappers.add(new ScheduleSlotWrapper(aBreakModel));
        }
    }

    @Override
    public boolean isVisible() {
        return Stream.of(scheduleSlotWrappers)
                .allMatch(new Predicate<ScheduleSlotWrapper>() {
                    @Override
                    public boolean test(ScheduleSlotWrapper value) {
                        return value.isVisible();
                    }
                });
    }

    @Override
    public int getSize() {
        return scheduleSlotWrappers.size();
    }

    @Override
    public int getItemType(int position) {
        return ScheduleDayLineupAdapter.BREAK_VIEW;
    }

    @Override
    public SlotApiModel getItem(int position) {
        return scheduleSlotWrappers.get(0).getSlotApiModel();
    }

    public SlotApiModel getBreakModel() {
        return scheduleSlotWrappers.get(0).getSlotApiModel();
    }
}

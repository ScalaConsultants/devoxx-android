package io.scalac.degree.android.adapter.schedule.model;

import java.util.ArrayList;
import java.util.List;

import io.scalac.degree.android.adapter.schedule.ScheduleDayLineupAdapter;
import io.scalac.degree.connection.model.SlotApiModel;

public class BreakScheduleItem extends ScheduleItem {

    private List<SlotApiModel> scheduleSlotWrappers;

    public BreakScheduleItem(
            long startTime,
            long endTime,
            int strIndex,
            int stpIndex,
            List<SlotApiModel> models) {
        super(startTime, endTime, strIndex, stpIndex);

        scheduleSlotWrappers = new ArrayList<>(models.size());
        for (SlotApiModel aBreakModel : models) {
            scheduleSlotWrappers.add(aBreakModel);
        }
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
        return scheduleSlotWrappers.get(0);
    }

    public SlotApiModel getBreakModel() {
        return scheduleSlotWrappers.get(0);
    }
}

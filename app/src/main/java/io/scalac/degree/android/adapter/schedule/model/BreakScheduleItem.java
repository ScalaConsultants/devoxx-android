package io.scalac.degree.android.adapter.schedule.model;

import java.util.ArrayList;
import java.util.List;

import io.scalac.degree.android.adapter.schedule.ScheduleDayLineupAdapter;
import io.scalac.degree.connection.model.SlotApiModel;

public class BreakScheduleItem extends ScheduleItem {

    private List<SlotApiModel> slotApiModels;

    public BreakScheduleItem(
            long startTime,
            long endTime,
            int strIndex,
            int stpIndex,
            List<SlotApiModel> models) {
        super(startTime, endTime, strIndex, stpIndex);

        slotApiModels = new ArrayList<>(models.size());
        for (SlotApiModel aBreakModel : models) {
            slotApiModels.add(aBreakModel);
        }
    }

    @Override
    public int getSize() {
        return slotApiModels.size();
    }

    @Override
    public int getItemType(int position) {
        return ScheduleDayLineupAdapter.BREAK_VIEW;
    }

    @Override
    public SlotApiModel getItem(int position) {
        return slotApiModels.get(0);
    }

    @Override
    public List<SlotApiModel> getAllItems() {
        return slotApiModels;
    }

    public SlotApiModel getBreakModel() {
        return slotApiModels.get(0);
    }
}

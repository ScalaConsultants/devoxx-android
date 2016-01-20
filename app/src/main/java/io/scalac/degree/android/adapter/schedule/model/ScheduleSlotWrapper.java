package io.scalac.degree.android.adapter.schedule.model;

import io.scalac.degree.connection.model.SlotApiModel;

public class ScheduleSlotWrapper {

    private final SlotApiModel slotApiModel;
    private boolean isVisible;

    public ScheduleSlotWrapper(SlotApiModel slotApiModel) {
        this.slotApiModel = slotApiModel;
        isVisible = true;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean vsiible) {
        isVisible = vsiible;
    }

    public SlotApiModel getSlotApiModel() {
        return slotApiModel;
    }
}

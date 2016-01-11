package io.scalac.degree.connection.model;

import java.io.Serializable;
import java.util.List;

public class SpecificScheduleApiModel implements Serializable {
    public final List<SlotApiModel> slots;

    public SpecificScheduleApiModel(List<SlotApiModel> slots) {
        this.slots = slots;
    }
}

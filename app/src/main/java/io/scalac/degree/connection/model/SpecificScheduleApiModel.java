package io.scalac.degree.connection.model;

import java.io.Serializable;
import java.util.List;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 27/10/2015
 */
public class SpecificScheduleApiModel implements Serializable {
    public final List<SlotApiModel> slots;

    public SpecificScheduleApiModel(List<SlotApiModel> slots) {
        this.slots = slots;
    }
}

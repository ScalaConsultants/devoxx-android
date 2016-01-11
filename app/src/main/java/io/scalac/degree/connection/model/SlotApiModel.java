package io.scalac.degree.connection.model;

import com.annimon.stream.function.Predicate;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SlotApiModel implements Serializable {

    /* nullability is mutually exclusive with talk field */
    @SerializedName("break")
    public BreakApiModel slotBreak;

    /* nullability is mutually exclusive with talk break */
    public TalkFullApiModel talk;

    public String roomId;
    public String roomSetup;
    public String toTime;
    public String fromTime;
    public String roomName;
    public String slotId;
    public String day;
    public boolean notAllocated;
    public long fromTimeMillis;
    public long toTimeMillis;
    public int roomCapacity;

    public boolean isBreak() {
        return slotBreak != null && talk == null;
    }

    public boolean isTalk() {
        return slotBreak == null && talk != null;
    }

    public static class SameModelPredicate implements Predicate<SlotApiModel> {

        private String id;

        public SameModelPredicate(String id) {
            this.id = id;
        }

        @Override
        public boolean test(SlotApiModel value) {
            return value.slotId.equals(id);
        }
    }
}

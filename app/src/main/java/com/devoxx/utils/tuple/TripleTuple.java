package com.devoxx.utils.tuple;

public class TripleTuple<First, Second, Third> {
    public final First first;
    public final Second second;
    public final Third object;

    public TripleTuple(First first, Second second, Third object) {
        this.first = first;
        this.second = second;
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripleTuple)) return false;

        TripleTuple<?, ?, ?> tripleTuple = (TripleTuple<?, ?, ?>) o;

        if (first != null ? !first.equals(tripleTuple.first) : tripleTuple.first != null) return false;
        return !(second != null ? !second.equals(tripleTuple.second) : tripleTuple.second != null);

    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }
}

package io.scalac.degree.utils;

public class Tuple<First, Second, Third> {
    public final First first;
    public final Second second;
    public final Third object;

    public Tuple(First first, Second second, Third object) {
        this.first = first;
        this.second = second;
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;

        Tuple<?, ?, ?> tuple = (Tuple<?, ?, ?>) o;

        if (first != null ? !first.equals(tuple.first) : tuple.first != null) return false;
        return !(second != null ? !second.equals(tuple.second) : tuple.second != null);

    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }
}

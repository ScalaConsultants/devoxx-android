package io.scalac.degree.utils.tuple;

public class Tuple<First, Second> {
    public final First first;
    public final Second second;

    public Tuple(First first, Second second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;

        Tuple<?, ?> that = (Tuple<?, ?>) o;

        return !(first != null ? !first.equals(that.first) : that.first != null);

    }

    @Override
    public int hashCode() {
        return first != null ? first.hashCode() : 0;
    }
}

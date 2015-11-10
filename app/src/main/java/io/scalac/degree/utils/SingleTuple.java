package io.scalac.degree.utils;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 29/10/2015
 */
public class SingleTuple<First, Second> {
	public final First first;
	public final Second object;

	public SingleTuple(First first, Second object) {
		this.first = first;
		this.object = object;
	}

	@Override public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SingleTuple)) return false;

		SingleTuple<?, ?> that = (SingleTuple<?, ?>) o;

		return !(first != null ? !first.equals(that.first) : that.first != null);

	}

	@Override public int hashCode() {
		return first != null ? first.hashCode() : 0;
	}
}

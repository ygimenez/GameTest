package com.kuuhaku.utils;

import java.util.Objects;

public class Delta<T> {
	private T prev, value;

	public Delta() {
	}

	public Delta(T value) {
		this.value = this.prev = value;
	}

	public T get() {
		return value;
	}

	public void set(T value) {
		this.prev = this.value;
		this.value = value;
	}

	public boolean changed() {
		return !Objects.equals(value, prev);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Delta<?> delta = (Delta<?>) o;
		return Objects.equals(value, delta.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}

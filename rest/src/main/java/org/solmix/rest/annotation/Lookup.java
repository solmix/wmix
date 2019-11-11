package org.solmix.rest.annotation;

public enum Lookup {
	NEW("new"), 
	CONTAINER("container");

	private final String value;

	Lookup(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static Lookup fromValue(String v) {
		for (Lookup c : Lookup.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
}

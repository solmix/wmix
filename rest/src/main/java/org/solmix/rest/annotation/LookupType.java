package org.solmix.rest.annotation;

public enum LookupType {
	NEW("new"), 
	CONTAINER("container");

	private final String value;

	LookupType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static LookupType fromValue(String v) {
		for (LookupType c : LookupType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
}

package com.sankar.injectingvalidator;

import java.lang.reflect.Type;

@SuppressWarnings("serial")
public class UnmatchedTypeException extends Exception {
	
	private final String accessPath; 
	private final Type expected;
	
	public UnmatchedTypeException(String accessPath, Type expected) {
		super(String.format("Unmatched type at access path [%s], expected [%s]", accessPath, expected));
		this.accessPath = accessPath;
		this.expected = expected;
	}

	public String getAccessPath() {
		return accessPath;
	}

	public Type getExpected() {
		return expected;
	}

}

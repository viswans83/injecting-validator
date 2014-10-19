package com.sankar.injectingvalidator;

@SuppressWarnings("serial")
public class IllegalValueException extends RuntimeException {
	
	private final String path;
	
	public IllegalValueException(String path) {
		super(String.format("Illegal value at access path: %s", path));
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}

}

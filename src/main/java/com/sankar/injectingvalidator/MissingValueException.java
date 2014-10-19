package com.sankar.injectingvalidator;

@SuppressWarnings("serial")
public class MissingValueException extends Exception {
	
	private final String path;
	
	public MissingValueException(String path) {
		super(String.format("Missing value for access path: %s", path));
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
}

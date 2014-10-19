package com.sankar.injectingvalidator;

@SuppressWarnings("serial")
public class UnresolvedParameterException extends RuntimeException {
	
	private final String path;
	
	public UnresolvedParameterException(String path) {
		super(String.format("Count not resolve: %s", path));
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
}
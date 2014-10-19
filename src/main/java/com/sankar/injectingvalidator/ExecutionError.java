package com.sankar.injectingvalidator;

@SuppressWarnings("serial")
public class ExecutionError extends RuntimeException {
	
	public ExecutionError(Throwable cause) {
		super(cause);
	}

}

package com.sankar.injectingvalidator;

@SuppressWarnings("serial")
public class RuleDefinitionError extends RuntimeException {
	
	public RuleDefinitionError(String message) {
		super(message);
	}

}

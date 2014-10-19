package com.sankar.injectingvalidator;

public interface Result {
	void fail(String message, Object... args);
	void failRule(String ruleId, String message, Object... args);
}

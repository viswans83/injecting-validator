package com.sankar.injectingvalidator;

public interface RuleFailureHandler {
	void failed(String ruleId, String messageKey, Object[] args);
}

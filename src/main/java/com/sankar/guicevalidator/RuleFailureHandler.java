package com.sankar.guicevalidator;

public interface RuleFailureHandler<T> extends Result {
	void setRuleName(String ruleName);
	void onFailure();
	T results();
}

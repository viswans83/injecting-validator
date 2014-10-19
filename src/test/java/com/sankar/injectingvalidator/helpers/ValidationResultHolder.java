package com.sankar.injectingvalidator.helpers;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import com.sankar.injectingvalidator.RuleFailureHandler;

public class ValidationResultHolder implements RuleFailureHandler {
	
	private Map<String, String> ruleFailures = new HashMap<>();

	@Override
	public void failed(String ruleId, String message, Object[] args) {
		ruleFailures.put(ruleId, MessageFormat.format(message, args));
	}
	
	public void print() {
		for(Map.Entry<String, String> item : ruleFailures.entrySet()) {
			System.out.printf("Failed [%s] - %s%n", item.getKey(), item.getValue());
		}
	}
	
	public void ensurePassing() {
		if (!ruleFailures.isEmpty())
			throw new RuntimeException("Validation failed");
	}
	
	public boolean gotFailure(String ruleId) {
		return ruleFailures.containsKey(ruleId);
	}
	
	public int failureCount() {
		return ruleFailures.size();
	}

}

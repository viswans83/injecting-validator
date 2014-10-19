package com.sankar.injectingvalidator;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

class ClassScan {
	
	private Class<?> ruleSetClass;
	private Set<RuleDefinition> scannedRules = new HashSet<>();
	
	private Set<String> ruleIds = new HashSet<>();
	private String currentRuleId;
	
	ClassScan(Class<?> ruleSetClass) {
		this.ruleSetClass = ruleSetClass;
	}
	
	String getCurrentRuleId() {
		return currentRuleId;
	}
	
	void ruleFound(Rule rule) {
		String ruleId = rule.value();
		
		if (ruleIds.contains(ruleId))
			RuleSetBuilder.fail("Duplicate ruleId found: %s", ruleId);
		
		else if (ruleId.trim().length() == 0)
			RuleSetBuilder.fail("Invalid ruleId: %s", ruleId);
		
		else
			ruleIds.add(ruleId);
		
		currentRuleId = ruleId;
	}
	
	void buildCurrentRule(Parameter[] parameters, Method method) {
		addRule(currentRuleId, parameters, method);
	}
	
	private void addRule(String ruleId, Parameter[] parameters, Method method) {
		scannedRules.add(new RuleDefinition(ruleId, parameters, method));
	}
	
	Class<?> getRuleSetClass() {
		return ruleSetClass;
	}
	
	Set<RuleDefinition> getScannedRules() {
		return scannedRules;
	}
	
}
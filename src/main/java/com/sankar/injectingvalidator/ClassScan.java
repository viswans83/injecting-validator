package com.sankar.injectingvalidator;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

class ClassScan {
	
	private Class<?> ruleSetClass;
	private Set<RuleDefinition> scannedRules = new HashSet<>();
	
	private Set<String> ruleIds = new HashSet<>();
	private String currentRuleId;
	private String pathPrefix;
	
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
		pathPrefix = null;
	}
	
	void setCurrentRulePathPrefix(String pathPrefix) {
		this.pathPrefix = pathPrefix;
	}
	
	void buildCurrentRule(Parameter[] parameters, Method method) {
		addRule(currentRuleId, parameters, method, pathPrefix);
	}
	
	private void addRule(String ruleId, Parameter[] parameters, Method method, String pathPrefix) {
		scannedRules.add(new RuleDefinition(ruleId, pathPrefix, parameters, method));
	}
	
	Class<?> getRuleSetClass() {
		return ruleSetClass;
	}
	
	Set<RuleDefinition> getScannedRules() {
		return scannedRules;
	}	
	
}
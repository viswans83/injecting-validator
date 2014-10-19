package com.sankar.guicevalidator;

import java.util.Set;

public class RuleSet {
	
	private final Class<?> ruleClass;
	private final Set<RuleDefinition> rules;
	
	RuleSet(Class<?> clazz, Set<RuleDefinition> rules) {
		this.ruleClass = clazz;
		this.rules = rules;
	}
	
	public static RuleSet from(Class<?> clazz) {
		return new RuleSetBuilder().scan(clazz).build();
	}
	
}

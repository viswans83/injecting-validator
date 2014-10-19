package com.sankar.injectingvalidator;

import java.lang.reflect.InvocationTargetException;
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
	
	void execute(DependencyResolver dependencyResolver, ValueResolver valueResolver, RuleFailureHandler handler) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Object instance = dependencyResolver.resolve(ruleClass);
		
		for(RuleDefinition rule : rules) {
			rule.execute(instance, valueResolver, handler);
		}
	}
	
}

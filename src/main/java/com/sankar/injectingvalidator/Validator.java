package com.sankar.injectingvalidator;

import java.lang.reflect.InvocationTargetException;

public class Validator {
	
	private RuleSet ruleSet;
	
	public Validator(RuleSet ruleSet) {
		this.ruleSet = ruleSet;
	}
	
	public void runRules(DependencyResolver dependencyResolver, ValueResolver valueResolver, RuleFailureHandler handler) {
		try {
			ruleSet.execute(dependencyResolver, valueResolver, handler);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ExecutionError(e);
		}
	}

}

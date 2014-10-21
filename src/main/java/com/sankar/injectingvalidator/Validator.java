package com.sankar.injectingvalidator;

import java.lang.reflect.InvocationTargetException;

public class Validator {
	
	private RuleSet[] ruleSets;
	
	public Validator(RuleSet... ruleSets) {
		this.ruleSets = ruleSets;
	}
	
	public void runRules(DependencyResolver dependencyResolver, ValueResolver valueResolver, RuleFailureHandler handler) {
		for (RuleSet ruleSet : ruleSets) {
			try {
				ruleSet.execute(dependencyResolver, valueResolver, handler);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | UnmatchedTypeException e) {
				throw new ValidationExecutionException(e);
			}
		}
	}

}

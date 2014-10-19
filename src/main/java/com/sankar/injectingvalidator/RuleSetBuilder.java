package com.sankar.injectingvalidator;

import java.lang.reflect.Method;

public class RuleSetBuilder {
	
	public UnfinishedRuleSetBuilder scan(Class<?> clazz) {
		ClassScan classScan = new ClassScan(clazz);
		
		for(Method m : clazz.getMethods()) {
			if (m.isAnnotationPresent(Rule.class)) {
				Rule rule = fetchRuleAnnotation(m);
				classScan.ruleFound(rule);
				
				MethodScan methodScan = new MethodScan(classScan, m);
				scanMethod(m, methodScan);
			}
		}
		
		return new UnfinishedRuleSetBuilder(classScan.getRuleSetClass(), classScan.getScannedRules());
	}
	
	private void scanMethod(Method m, MethodScan methodScan) {
		RuleMethodArgumentInfo[] arguments = RuleMethodArgumentInfo.buildFrom(m);
		
		for(RuleMethodArgumentInfo argument : arguments) {
			argument.informAboutSelf(methodScan);
		}
		
		methodScan.complete();
	}
	
	private Rule fetchRuleAnnotation(Method m) {
		return m.getAnnotation(Rule.class);
	}
	
	static void fail(String reason) {
		throw new RuleDefinitionError(reason);
	}
	
	static void fail(String format, Object... args) {
		fail(String.format(format, args));
	}

}

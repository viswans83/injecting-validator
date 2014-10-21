package com.sankar.injectingvalidator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UnfinishedRuleSetBuilder {
	
	private Class<?> ruleSetClass;
	private Set<RuleDefinition> rules;
	
	public UnfinishedRuleSetBuilder(Class<?> ruleSetClass, Set<RuleDefinition> rules) {
		this.ruleSetClass = ruleSetClass;
		this.rules = rules;
	}
	
	public UnfinishedRuleSetBuilder keeping(String... ruleIds) {
		Set<RuleDefinition> filtered = new HashSet<>();
		
		Map<String, RuleDefinition> ruleMap = map();
		for(String rule : ruleIds) {
			filtered.add(ruleMap.get(rule));
		}
		this.rules = filtered;
		
		return this;
	}
	
	public UnfinishedRuleSetBuilder removing(String... ruleIds) {
		Map<String, RuleDefinition> ruleMap = map();
		
		for(String rule : ruleIds) {
			rules.remove(ruleMap.get(rule));
		}
		
		return this;
	}
	
	public RuleSet build() {
		if (rules.isEmpty())
			RuleSetBuilder.fail("RuleSet was empty");
		
		return new RuleSet(ruleSetClass, rules);
	}
	
	private Map<String, RuleDefinition> map() {
		Map<String, RuleDefinition> result = new HashMap<>();
		
		for(RuleDefinition rule : rules) {
			result.put(rule.getRuleId(), rule);
		}
		
		return result;
	}
	
}
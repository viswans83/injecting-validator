package com.sankar.guicevalidator;

import java.lang.reflect.Method;

public class RuleDefinition {
	
	private final String ruleId;
	private final RuleParameter[] parameters;
	private final Method method;
	
	public RuleDefinition(String ruleId, RuleParameter[] parameters, Method method) {
		this.ruleId = ruleId;
		this.parameters = parameters;
		this.method = method;
	}
	
	public String getRuleId() {
		return ruleId;
	}
	
	public int parameterCount() {
		return parameters.length;
	}
	
	public boolean isResultHolder(int parameterIndex) {
		return parameters[parameterIndex].isResultHolder();
	}

}

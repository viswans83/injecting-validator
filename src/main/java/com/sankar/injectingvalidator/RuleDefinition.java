package com.sankar.injectingvalidator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class RuleDefinition {
	
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
	
	private int parameterCount() {
		return parameters.length;
	}
	
	private boolean isResultHolder(int parameterIndex) {
		return parameters[parameterIndex].isResultHolder();
	}
	
	void execute(Object instance, ValueResolver valueResolver, RuleFailureHandler handler) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(instance, buildArguments(valueResolver, handler));
	}
	
	private Object[] buildArguments(ValueResolver valueResolver, RuleFailureHandler handler) {
		Object[] args = new Object[parameterCount()];
		
		for(int i = 0; i < parameterCount(); i++)
			args[i] = isResultHolder(i) ? 
					buildResultProxy(handler) : resolveParameter(i, valueResolver);
		
		return args;
	}

	private Object resolveParameter(int i, ValueResolver valueResolver) {
		return parameters[i].resolve(valueResolver);
	}

	private Result buildResultProxy(final RuleFailureHandler handler) {
		return new Result() {
			@Override
			public void fail(String message, Object... args) {
				handler.failed(ruleId, message, args);
			}
		};
	}
	

}

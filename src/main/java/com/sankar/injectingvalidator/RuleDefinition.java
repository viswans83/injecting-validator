package com.sankar.injectingvalidator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class RuleDefinition {
	
	private final String ruleId;
	private final Parameter[] parameters;
	private final Method method;
	
	public RuleDefinition(String ruleId, Parameter[] parameters, Method method) {
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
	
	private boolean isResultParameter(int parameterIndex) {
		return parameters[parameterIndex].isResultParameter();
	}
	
	void execute(Object instance, ValueResolver valueResolver, RuleFailureHandler handler) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, MissingValueException, UnmatchedTypeException {
		method.invoke(instance, buildArguments(valueResolver, handler));
	}
	
	private Object[] buildArguments(ValueResolver valueResolver, RuleFailureHandler handler) throws MissingValueException, UnmatchedTypeException {
		Object[] args = new Object[parameterCount()];
		
		for(int i = 0; i < parameterCount(); i++)
			args[i] = isResultParameter(i) ? 
					buildResultProxy(handler) : resolveParameter(i, valueResolver);
		
		return args;
	}

	private Object resolveParameter(int i, ValueResolver valueResolver) throws MissingValueException, UnmatchedTypeException {
		return parameters[i].resolve(valueResolver);
	}

	private Result buildResultProxy(final RuleFailureHandler handler) {
		return new Result() {
			@Override
			public void fail(String message, Object... args) {
				handler.failed(ruleId, message, args);
			}
			
			@Override
			public void failRule(String ruleId, String message, Object... args) {
				handler.failed(ruleId, message, args);
			}
		};
	}
	

}

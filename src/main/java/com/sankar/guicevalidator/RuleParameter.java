package com.sankar.guicevalidator;

public class RuleParameter {
	
	public static final RuleParameter RESULT = new RuleParameter(null, Result.class, false);
	
	private final String accessPath;
	private final Class<?> type;
	private final boolean optional;
	
	public RuleParameter(String accessPath, Class<?> type, boolean optional) {
		this.accessPath = accessPath;
		this.type = type;
		this.optional = optional;
	}

	public String getAccessPath() {
		return accessPath;
	}

	public Class<?> getType() {
		return type;
	}

	public boolean isOptional() {
		return optional;
	}
	
	public boolean isResultHolder() {
		return Result.class.equals(type);
	}
	
	public boolean acceptsValue(Object value) {
		if (value == null)
			return optional;
		else
			return type.isAssignableFrom(value.getClass());
	}

}

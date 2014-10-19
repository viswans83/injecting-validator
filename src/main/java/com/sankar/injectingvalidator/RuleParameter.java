package com.sankar.injectingvalidator;

class RuleParameter {
	
	static final RuleParameter RESULT_OBJECT = new RuleParameter(null, Result.class, false);
	
	private final String accessPath;
	private final Class<?> type;
	private final boolean optional;
	
	public RuleParameter(String accessPath, Class<?> type, boolean optional) {
		this.accessPath = accessPath;
		this.type = type;
		this.optional = optional;
	}
	
	boolean isResultHolder() {
		return Result.class.equals(type);
	}
	
	Object resolve(ValueResolver valueResolver) {
		if (isResultHolder())
			throw new AssertionError();
		
		Object value = valueResolver.lookup(accessPath);
		
		if (value == null)
			if (optional)
				return null;
			
			else
				throw new MissingValueException(accessPath);
		
		
		if (!type.isAssignableFrom(value.getClass()))
			throw new IllegalValueException(accessPath);
		
		return value;
	}

}

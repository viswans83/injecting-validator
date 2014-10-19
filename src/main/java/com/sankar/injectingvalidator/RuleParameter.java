package com.sankar.injectingvalidator;

class RuleParameter implements Parameter {
	
	private final String accessPath;
	private final Class<?> type;
	private final boolean optional;
	
	public RuleParameter(String accessPath, Class<?> type, boolean optional) {
		this.accessPath = accessPath;
		this.type = type;
		this.optional = optional;
	}
	
	public boolean isResultParameter() {
		return false;
	}
	
	public Object resolve(ValueResolver valueResolver) throws MissingValueException {
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

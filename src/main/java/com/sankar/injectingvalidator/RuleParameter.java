package com.sankar.injectingvalidator;

import java.lang.reflect.Type;

class RuleParameter implements Parameter {
	
	private final String accessPath;
	private final Class<?> type;
	private final Type genericType;
	private final boolean optional;
	
	public RuleParameter(String accessPath, Class<?> type, Type genericType, boolean optional) {
		this.accessPath = accessPath;
		this.type = type;
		this.genericType = genericType;
		this.optional = optional;
	}
	
	public boolean isResultParameter() {
		return false;
	}
	
	public Object resolve(ValueResolver valueResolver) throws MissingValueException, UnmatchedTypeException {
		Object value = valueResolver.lookup(accessPath, type, genericType);
		
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

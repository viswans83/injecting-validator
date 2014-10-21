package com.sankar.injectingvalidator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

class RuleMethodArgumentInfo {
	
	private final Class<?> type;
	private final Type genericType;
	private final Annotation[] annotations;
	
	RuleMethodArgumentInfo(Class<?> type, Type genericType, Annotation[] annotations) {
		this.type = type;
		this.genericType = genericType;
		this.annotations = annotations;
	}
	
	static RuleMethodArgumentInfo[] buildFrom(Method m) {
		Class<?>[] parameterTypes = m.getParameterTypes();
		Type[] genericParameterTypes = m.getGenericParameterTypes();
		Annotation[][] annotations = m.getParameterAnnotations();
		
		RuleMethodArgumentInfo[] result = new RuleMethodArgumentInfo[parameterTypes.length];
		
		for(int i = 0; i < result.length; i++) {
			result[i] = new RuleMethodArgumentInfo(parameterTypes[i], genericParameterTypes[i], annotations[i]);
		}
		
		return result;
	}
	
	void informAboutSelf(MethodScan methodScan) {
		if (isResultType()) {
			ensureValidResultParameterAnnotations();
			methodScan.resultParameterFound();
		}
		
		else {
			ensureValidRuleParameterAnnotations();
			methodScan.ruleParameterFound(getPathAnnotation().value(), type, genericType, isOptional());
		}
	}
	
	private boolean isResultType() {
		return Result.class.equals(type);
	}
	
	private void ensureValidResultParameterAnnotations() {
		for(Annotation a : annotations)
			if (Path.class.equals(a.annotationType()) || Optional.class.equals(a.annotationType()))
				RuleSetBuilder.fail("Result parameter annotated with @Path or @Optional");
	}
	
	private void ensureValidRuleParameterAnnotations() {
		for(Annotation a : annotations)
			if (Path.class.equals(a.annotationType()))
				if (getPathAnnotation().value().trim().length() > 0)
					return;
		
		RuleSetBuilder.fail("Rule parameters should be annotated with @Path containing a valid access path");
	}
	
	private Path getPathAnnotation() {
		for(Annotation ann : annotations) {
			if (Path.class.equals(ann.annotationType()))
				return (Path)ann;
		}
		
		throw new AssertionError();
	}
	
	private boolean isOptional() {
		for(Annotation annotation : annotations) {
			if (Optional.class.equals(annotation.annotationType()))
				return true;
		}
		
		return false;
	}
	
}
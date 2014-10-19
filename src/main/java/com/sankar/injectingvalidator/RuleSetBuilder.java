package com.sankar.injectingvalidator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RuleSetBuilder {
	
	private Class<?> ruleSetClass;
	private Set<RuleDefinition> rules;
	
	public RuleSetBuilder scan(Class<?> clazz) {
		ClassScan classScan = new ClassScan();
		
		for(Method m : clazz.getMethods()) {
			if (m.isAnnotationPresent(Rule.class)) {
				Rule rule = fetchRuleAnnotation(m);
				classScan.ruleFound(rule);
				
				MethodScan methodScan = new MethodScan(classScan, m);
				scanMethod(m, methodScan);
			}
		}
		
		classScan.complete();
		
		ruleSetClass = clazz;
		
		return RuleSetBuilder.this;
	}
	
	public RuleSet build() {
		if (ruleSetClass == null)
			fail("Class containing rules not provided");
		
		if (rules.isEmpty())
			fail("No rules were found");
		
		return new RuleSet(ruleSetClass, rules);
	}
	
	private void scanMethod(Method m, MethodScan methodScan) {
		Class<?>[] parameterTypes = m.getParameterTypes();
		Annotation[][] annotations = m.getParameterAnnotations();
		
		for(int i = 0; i < parameterTypes.length; i++) {
			Class<?> parameterType = parameterTypes[i];
			Annotation[] parameterAnnotations = annotations[i];
			
			if (Result.class.equals(parameterType)) {
				ensureValidResultParameterAnnotations(parameterAnnotations);
				methodScan.resultParameterFound();
			}
			
			else {
				ensureValidRuleParameterAnnotations(parameterAnnotations);
				
				Path path = getPathAnnotation(parameterAnnotations);
				boolean optional = isOptional(parameterAnnotations);
				
				methodScan.ruleParameterFound(path.value(), parameterType, optional);
			}
		}
		
		methodScan.complete();
	}
	
	private Rule fetchRuleAnnotation(Method m) {
		return m.getAnnotation(Rule.class);
	}

	private void ensureValidResultParameterAnnotations(Annotation[] parameterAnnotations) {
		for(Annotation a : parameterAnnotations)
			if (Path.class.equals(a.annotationType()) || Optional.class.equals(a.annotationType()))
				fail("Result parameter annotated with @Path or @Optional");
	}
	
	private void ensureValidRuleParameterAnnotations(Annotation[] parameterAnnotations) {
		for(Annotation a : parameterAnnotations)
			if (Path.class.equals(a.annotationType()))
				return;
		
		fail("Rule parameters should be annotated with @Path");
	}

	private Path getPathAnnotation(Annotation[] annotationsOnParamter) {
		for(Annotation ann : annotationsOnParamter) {
			if (Path.class.equals(ann.annotationType()))
				return (Path)ann;
		}
		
		throw new AssertionError();
	}
	
	private boolean isOptional(Annotation[] annotationsOnParamter) {
		for(Annotation annotation : annotationsOnParamter) {
			if (Optional.class.equals(annotation.annotationType()))
				return true;
		}
		
		return false;
	}
	
	private static void fail(String reason) {
		throw new RuleDefinitionError(reason);
	}
	
	private static void fail(String format, Object... args) {
		fail(String.format(format, args));
	}
	
	
	class ClassScan {
		
		private Set<RuleDefinition> scannedRules = new HashSet<>();
		
		private Set<String> ruleIds = new HashSet<>();
		private String currentRuleId;
		
		public String getCurrentRuleId() {
			return currentRuleId;
		}
		
		public void ruleFound(Rule rule) {
			String ruleId = rule.value();
			
			if (ruleIds.contains(ruleId))
				fail("Duplicate ruleId found: %s", ruleId);
			
			else if (ruleId.trim().length() == 0)
				fail("Invalid ruleId: %s", ruleId);
			
			else
				ruleIds.add(ruleId);
			
			currentRuleId = ruleId;
		}
		
		public void complete() {
			RuleSetBuilder.this.rules = scannedRules;
		}
		
		public void buildCurrentRule(Parameter[] parameters, Method method) {
			addRule(currentRuleId, parameters, method);
		}
		
		private void addRule(String ruleId, Parameter[] parameters, Method method) {
			scannedRules.add(new RuleDefinition(ruleId, parameters, method));
		}
		
	}
	
	
	class MethodScan {
		
		private ClassScan classScan;
		private Method method;
		
		private List<Parameter> parameters = new ArrayList<>();
		
		private boolean resultParameterFound;
		private Set<String> accessPathSet = new HashSet<>();
		
		public MethodScan(ClassScan classScan, Method m) {
			this.classScan = classScan;
			this.method = m;
		}
		
		public void ruleParameterFound(String path, Class<?> type, boolean optional) {
			if (path.trim().length() == 0)
				fail("Invalid access path [%s] found in ruleId [%s]", path, classScan.getCurrentRuleId());
			
			else if (accessPathSet.contains(path))
				fail("Duplicate access path [%s] found in ruleId [%s]", path, classScan.getCurrentRuleId());
			
			else
				addRuleParameter(path, type, optional);
		}
		
		public void resultParameterFound() {
			if (resultParameterFound)
				fail("Multiple result parameters found in ruleId: %s", classScan.getCurrentRuleId());
			
			else
				addParameter(ResultParameter.INSTANCE);
			
			resultParameterFound = true;
		}
		
		public void complete() {
			if (resultParameterFound)
				classScan.buildCurrentRule(parameters.toArray(new Parameter[0]), method);
			
			else
				fail("ruleId [%s] is missing a Result parameter", classScan.getCurrentRuleId());
		}
		
		private void addRuleParameter(String path, Class<?> type, boolean optional) {
			addParameter(new RuleParameter(path, type, optional));
		}
		
		private void addParameter(Parameter parameter) {
			parameters.add(parameter);
		}
		
	}

}

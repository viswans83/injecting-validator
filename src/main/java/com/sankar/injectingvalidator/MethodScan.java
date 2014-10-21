package com.sankar.injectingvalidator;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class MethodScan {
	
	private ClassScan classScan;
	private Method method;
	private String pathPrefix;
	private List<Parameter> parameters = new ArrayList<>();
	
	private boolean resultParameterFound;
	private Set<String> accessPathSet = new HashSet<>();
	
	public MethodScan(ClassScan classScan, Method m) {
		this.classScan = classScan;
		this.method = m;
		
		handlePathOnMethod();
	}
	
	private void handlePathOnMethod() {
		if (method.isAnnotationPresent(Path.class)) {
			pathPrefix = method.getAnnotation(Path.class).value();
			
			if (pathPrefix.trim().length() == 0)
				RuleSetBuilder.fail("Invalid access path on rule [{0}]", classScan.getCurrentRuleId());
			
			classScan.setCurrentRulePathPrefix(pathPrefix);
		}
	}

	void ruleParameterFound(String path, Class<?> type, Type genericType, boolean optional) {
		if (path.trim().length() == 0)
			RuleSetBuilder.fail("Invalid access path [%s] found in ruleId [%s]", path, classScan.getCurrentRuleId());
		
		else if (accessPathSet.contains(path))
			RuleSetBuilder.fail("Duplicate access path [%s] found in ruleId [%s]", path, classScan.getCurrentRuleId());
		
		else
			addRuleParameter(path, type, genericType, optional);
	}
	
	void resultParameterFound() {
		if (resultParameterFound)
			RuleSetBuilder.fail("Multiple result parameters found in ruleId: %s", classScan.getCurrentRuleId());
		
		else
			addParameter(ResultParameter.INSTANCE);
		
		resultParameterFound = true;
	}
	
	void complete() {
		if (resultParameterFound)
			classScan.buildCurrentRule(parameters.toArray(new Parameter[0]), method);
		
		else
			RuleSetBuilder.fail("ruleId [%s] is missing a Result parameter", classScan.getCurrentRuleId());
	}
	
	private void addRuleParameter(String path, Class<?> type, Type genericType, boolean optional) {
		addParameter(new RuleParameter(path, type, genericType, optional));
	}
	
	private void addParameter(Parameter parameter) {
		parameters.add(parameter);
	}
	
}
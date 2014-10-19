package com.sankar.guicevalidator;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.inject.Injector;

public class ValidationEngine<H extends RuleFailureHandler<R>, R> {
	
	private Injector injector;
	private Object context;
	private Class<R> resultsClass;
	
	ValidationEngine(Injector injector, Object context, Class<R> resultsClass) {
		this.injector = injector;
		this.context = context;
		this.resultsClass = resultsClass;
	}
	
	public R validate(Class<?>... validators) {
		
		for(Class<?> clazz : validators) {
			runRulesIn(clazz);
		}
		
		return injector.getInstance(resultsClass);
	}
	
	public static <HH extends RuleFailureHandler<RR>, RR> ValidationEngineBuilder<HH,RR> create() {
		return new ValidationEngineBuilder<HH,RR>();
	}
	
	private void runRulesIn(Class<?> ruleCollectionClass) {
		Collection<Method> rules = discoverRulesIn(ruleCollectionClass);
		
		if (rules.isEmpty()) return;
		
		Object validatorInstance = injector.getInstance(ruleCollectionClass);
		
		for(Map.Entry<Method, Object[]> item : filterRules(rules).entrySet()) {
			Method rule = item.getKey();
			Object[] params = item.getValue();
			
			try {
				rule.invoke(validatorInstance, params);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	private List<Method> discoverRulesIn(Class<?> clazz) {
		List<Method> result = new ArrayList<>();
		
		for(Method m : clazz.getMethods()) {
			if (m.isAnnotationPresent(Rule.class))
				result.add(m);
		}
		
		return result;
	}
	
	private Map<Method, Object[]> filterRules(Collection<Method> rules) {
		Map<Method, Object[]> methodCalls = new HashMap<>();
		
		outer: for(Method m : rules) {
			Rule rule = m.getAnnotation(Rule.class);
			
			Class<?>[] types = m.getParameterTypes();
			Annotation[][] annotations = m.getParameterAnnotations();
			
			Object[] params = new Object[types.length];
			
			int resultObjectIndex = -1;
			
			for(int i = 0; i < types.length; i++) {
				Path path = null;
				
				if (Result.class.equals(types[i]))
					resultObjectIndex = i;
				
				else if ((path = getPath(annotations[i])) != null) {
					boolean optional = isOptional(annotations[i]);
					try {
						Object value = PropertyUtils.getNestedProperty(context, path.value());
						
						if (value == null && !optional)
							continue outer;
						else if (value != null)
							params[i] = value;
						
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						if (!optional)
							continue outer;
					}
				}
			}
			
			if (resultObjectIndex >= 0) {
				RuleFailureHandler<?> rfh = injector.getInstance(RuleFailureHandler.class);
				
				rfh.setRuleName(rule.value());
				params[resultObjectIndex] = rfh;
				
				methodCalls.put(m, params);
			}
		}
		
		return methodCalls;
	}
	
	private boolean isOptional(Annotation[] annotationsOnParamter) {
		for(Annotation annotation : annotationsOnParamter) {
			if (annotation.annotationType().equals(Optional.class))
				return true;
		}
		
		return false;
	}
	
	private Path getPath(Annotation[] annotationsOnParamter) {
		for(Annotation ann : annotationsOnParamter) {
			if (ann.annotationType().equals(Path.class))
				return (Path)ann;
		}
		
		return null;
	}

}

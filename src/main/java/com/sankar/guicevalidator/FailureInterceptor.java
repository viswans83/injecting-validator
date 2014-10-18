package com.sankar.guicevalidator;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class FailureInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object result = invocation.proceed();
		
		RuleFailureHandler<?> handler = (RuleFailureHandler<?>)invocation.getThis();
		handler.onFailure();
		
		return result;
	}

}

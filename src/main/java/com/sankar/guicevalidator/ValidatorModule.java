package com.sankar.guicevalidator;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;

public class ValidatorModule<H extends RuleFailureHandler<R>, R> extends AbstractModule {
	
	private Class<H> failureHandlingClass;
	private Class<R> resultsClass;
	
	public ValidatorModule(Class<H> failureHandlingClass, Class<R> resultsClass) {
		this.failureHandlingClass = failureHandlingClass;
		this.resultsClass = resultsClass;
	}

	@Override
	protected void configure() {
		bind(resultsClass).in(Singleton.class);
		bind(RuleFailureHandler.class).to(failureHandlingClass);
		bindInterceptor(Matchers.only(failureHandlingClass), Matchers.annotatedWith(FailureMethod.class), new FailureInterceptor());
	}

}

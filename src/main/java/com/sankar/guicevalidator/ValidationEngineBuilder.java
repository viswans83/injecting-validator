package com.sankar.guicevalidator;

import java.util.ArrayList;
import java.util.Collection;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class ValidationEngineBuilder<H extends RuleFailureHandler<R>, R> {
	
	private Class<H> handlerClass;
	private Class<R> resultsClass;
	private Module[] modules;
	private Object context;
	
	public ValidationEngineBuilder<H,R> withRuleFailureHandler(Class<H> clazz) {
		this.handlerClass = clazz;
		return this;
	}
	
	public ValidationEngineBuilder<H,R> withResultCollector(Class<R> clazz) {
		this.resultsClass = clazz;
		return this;
	}
	
	public ValidationEngineBuilder<H,R> withModules(Module... modules) {
		this.modules = modules;
		return this;
	}
	
	public ValidationEngineBuilder<H,R> withContext(Object context) {
		this.context = context;
		return this;
	}
	
	public ValidationEngine<H,R> build() {
		Collection<Module> injectionModules = new ArrayList<>();
		
		injectionModules.add(new ValidatorModule<>(handlerClass, resultsClass));
		for(Module m : modules) {
			injectionModules.add(m);
		}
		
		Injector injector = Guice.createInjector(injectionModules);
		
		return new ValidationEngine<H,R>(injector, context, resultsClass);
	}

}

package com.sankar.injectingvalidator;

public interface DependencyResolver {
	<T> T resolve(Class<T> clazz); 
}

package com.sankar.injectingvalidator;

public interface ValueResolver {
	Object lookup(String path);
}

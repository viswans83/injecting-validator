package com.sankar.injectingvalidator;

public interface Result {
	void fail(String message, Object... args);
}

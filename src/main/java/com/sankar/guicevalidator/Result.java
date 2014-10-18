package com.sankar.guicevalidator;

public interface Result {
	void fail(String message, Object... args);
}

package com.sankar.injectingvalidator;

interface Parameter {
	boolean isResultParameter();
	Object resolve(ValueResolver valueResolver) throws MissingValueException, UnmatchedTypeException;
}
